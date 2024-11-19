package io.mrnateriver.smsproxy.models

import kotlin.String
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class MessageProxyRequest(
  @SerialName("receiverKey")
  public val receiverKey: String,
  @SerialName("sender")
  public val sender: String,
  @SerialName("message")
  public val message: String,
  @SerialName("receivedAt")
  public val receivedAt: Instant,
)
