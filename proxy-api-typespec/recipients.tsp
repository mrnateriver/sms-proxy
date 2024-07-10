using TypeSpec.Http;

namespace ProxyService;

@doc("Parameters of a message recipient that can change over time.")
model RecipientTransientParams {
  @doc("Opaque identifier of the recipient in the notification system. For example, device token in Firebase Cloud Messaging.")
  notificationsId: string;
}

model RegisterRecipientRequest {
  ...RecipientTransientParams;

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
