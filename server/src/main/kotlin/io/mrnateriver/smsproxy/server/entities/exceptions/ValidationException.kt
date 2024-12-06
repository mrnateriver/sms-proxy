package io.mrnateriver.smsproxy.server.entities.exceptions

class ValidationException(
    val errors: Map<String, List<String>>,
    cause: Throwable? = null,
) :
    Exception("Request validation failed", cause)
