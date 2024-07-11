using TypeSpec.Http;

namespace ProxyService;

model MessageProxyRequest {
  @doc("Random key of the end receiver of the proxied message.")
  receiverKey: string;

  sender: string;
  message: string;
  receivedAt: offsetDateTime;
}

model MessageProxyResponse {
  ...CreatedResponse;

  @doc("ID of the message entry in the intermediary system.")
  externalId: string;
}

@route("/messages")
namespace Messages {
  @post
  @route("/proxy")
  @doc("Proxy a message, recording it the intermediary system and immediately forwarding it to the intended receiver. For security reasons, doesn't expose if the receiver is not registered.")
  op proxy is ProxyApiMethod.run<
    {
      @body request: MessageProxyRequest;
    },
    MessageProxyResponse
  >;
}
