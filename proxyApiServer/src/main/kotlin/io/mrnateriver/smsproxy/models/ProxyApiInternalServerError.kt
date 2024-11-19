package io.mrnateriver.smsproxy.models

import kotlin.Int
import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ProxyApiInternalServerError(
  @SerialName("code")
  public val code: Int,
  @SerialName("message")
  public val message: String,
)
