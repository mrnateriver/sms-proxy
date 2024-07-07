import "@typespec/http";
import "@typespec/rest";
import "@typespec/openapi3";

using TypeSpec.Http;
using TypeSpec.Rest;

@doc("Service for proxying messages. Uses random API keys for authentication, which are passed as HTTP Bearer tokens.")
@useAuth(BearerAuth)
@service({
  title: "Service for proxying messages",
})
namespace ProxyService;

model MessageProxyRequest {
  @doc("Random key of the end recipient of the proxied message.")
  recipientKey: string;

  sender: string;
  message: string;
  receivedAt: offsetDateTime;
}

model MessageProxyResponse extends CreatedResponse {
  @doc("ID of the message entry in the intermediary system.")
  externalId: string;
}

@error
@doc("Unexpected internal server error occurred.")
model MessageProxyError<T extends uint32> {
  @statusCode statusCode: T;
  @doc("Machine-readable error code.") code: uint32;
  @doc("Human-readable explanation of the error.") message: string;
}

@error
@doc("Unexpected internal server error occurred.")
model MessageProxyInternalServerError is MessageProxyError<500>;

@error
@doc("Value was formatted properly but contained invalid data.")
model MessageProxyValidationError<T extends uint32>
  extends MessageProxyError<T> {
  invalidFields: {
    field: string;
    message: string;
  }[];
}

interface ProxyApiMethod {
  run<Payload, SuccessfulResponse>(...Payload):
    | SuccessfulResponse
    | MessageProxyError<400>
    | MessageProxyError<401>
    | MessageProxyError<503>
    | MessageProxyInternalServerError
    | MessageProxyValidationError<422>;
}

@route("/messages")
namespace Messages {
  @post
  @route("/proxy")
  @doc("Proxy a message, recording it the intermediary system and immediately forwarding it to the intended recipient. For security reasons, doesn't expose if the recipient is not registered.")
  op proxy is ProxyApiMethod.run<
    {
      @body request: MessageProxyRequest;
    },
    MessageProxyResponse
  >;
}

@doc("Parameters of a message recipient that can change over time.")
model RecipientTransientParams {
  @doc("Opaque identifier of the recipient in the notification system. For example, device token in Firebase Cloud Messaging.")
  notificationsId: string;
}

model RegisterRecipientRequest extends RecipientTransientParams {
  @doc("Random key of the recipient.")
  recipientKey: string;

  @doc("Public key of the recipient for encrypting sent out messages.")
  publicKey: bytes;
}

@route("/recipients")
namespace Recipients {
  @post
  @route("/register")
  @doc("Register a recipient in the intermediary system. If a recipient with the specified key is already registered, the request is silently ignored for security reasons.")
  op register is ProxyApiMethod.run<
    {
      @body payload: RegisterRecipientRequest;
    },
    OkResponse
  >;

  @patch
  @route("/{recipientKey}")
  @doc("Updates transient parameters of the specified recipient. ")
  op update is ProxyApiMethod.run<
    {
      @path("recipientKey") recipientKey: string;
      @body payload: RecipientTransientParams;
    },
    OkResponse
  >;
}
