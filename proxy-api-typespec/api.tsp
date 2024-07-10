using TypeSpec.Http;

namespace ProxyService;

@error
@doc("Unexpected internal server error occurred.")
model ProxyApiError<T extends uint32> {
  @statusCode statusCode: T;
  @doc("Machine-readable error code.") code: uint32;
  @doc("Human-readable explanation of the error.") message: string;
}

@error
@doc("Unexpected internal server error occurred.")
model ProxyApiInternalServerError is ProxyApiError<500>;

@error
@doc("Value was formatted properly but contained invalid data.")
model ProxyApiValidationError<T extends uint32> {
  ...ProxyApiError<T>;
  invalidFields: {
    field: string;
    message: string;
  }[];
}

interface ProxyApiMethod {
  run<Payload, SuccessfulResponse>(...Payload):
    | SuccessfulResponse
    | ProxyApiError<400>
    | ProxyApiError<401>
    | ProxyApiError<503>
    | ProxyApiInternalServerError
    | ProxyApiValidationError<422>;
}
