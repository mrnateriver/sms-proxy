import "@typespec/http";
import "./messages.tsp";
import "./recipients.tsp";
import "./api.tsp";

using TypeSpec.Http;

@doc("Service for proxying messages. Uses random API keys for authentication, which are passed as HTTP Bearer tokens.")
@useAuth(BearerAuth)
@service({
  title: "Service for proxying messages",
})
namespace ProxyService;
