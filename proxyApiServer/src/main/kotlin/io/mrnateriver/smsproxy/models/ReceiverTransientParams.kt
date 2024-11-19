package io.mrnateriver.smsproxy.models

import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ReceiverTransientParams(
  @SerialName("notificationsId")
  public val notificationsId: String? = null,
)
