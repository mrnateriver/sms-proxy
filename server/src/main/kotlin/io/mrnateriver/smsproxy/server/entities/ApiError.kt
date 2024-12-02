package io.mrnateriver.smsproxy.server.entities

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    val code: Int,
    val message: String,
    val errors: Map<String, List<String>> = emptyMap(),
)
