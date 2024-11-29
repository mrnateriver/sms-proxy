package io.mrnateriver.smsproxy.models

import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class RegisterReceiverRequest(
  @SerialName("notificationsId")
  public val notificationsId: String,
  @SerialName("receiverKey")
  public val receiverKey: String,
  @SerialName("publicKey")
  public val publicKey: String,
)
