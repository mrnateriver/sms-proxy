using TypeSpec.Http;

namespace ProxyService;

model MessageProxyRequest {
  @doc("Random key of the end recipient of the proxied message.")
  recipientKey: string;

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
@doc("Service for proxying messages. Uses random API keys for authentication, which are passed as HTTP Bearer tokens.")
@useAuth(BearerAuth)
@service({
  title: "Service for proxying messages",
})
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
