using TypeSpec.Http;

namespace ProxyService;

@doc("Parameters of a message receiver that can change over time.")
model ReceiverTransientParams {
  @doc("Opaque identifier of the receiver in the notification system. For example, device token in Firebase Cloud Messaging.")
  notificationsId: string;
}

model RegisterReceiverRequest {
  ...ReceiverTransientParams;

  @doc("Random key of the receiver.")
  receiverKey: string;

  @doc("Base64-encoded public key of the receiver for encrypting sent out messages.")
  publicKey: string;
}

@route("/receivers")
namespace Receivers {
  @post
  @route("/register")
  @doc("Register a receiver in the intermediary system. If a receiver with the specified key is already registered, the request is silently ignored for security reasons.")
  op register is ProxyApiMethod.run<
    {
      @body payload: RegisterReceiverRequest;
    },
    OkResponse
  >;

  @patch
  @route("/{receiverKey}")
  @doc("Updates transient parameters of the specified receiver. ")
  op update is ProxyApiMethod.run<
    {
      @path("receiverKey") receiverKey: string;
      @body payload: ReceiverTransientParams;
    },
    OkResponse
  >;
}
