package io.mrnateriver.smsproxy.models

import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class MessageProxyResponse(
  @SerialName("externalId")
  public val externalId: String,
)
