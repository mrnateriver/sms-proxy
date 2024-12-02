package io.mrnateriver.smsproxy.server.entities.exceptions

class ValidationException(
    val errors: Map<String, List<String>>,
    override val message: String,
    cause: Throwable? = null,
) :
    Exception(message, cause)
