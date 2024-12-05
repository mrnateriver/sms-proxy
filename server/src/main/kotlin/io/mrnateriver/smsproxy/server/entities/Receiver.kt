package io.mrnateriver.smsproxy.server.entities

import kotlinx.datetime.Instant
import java.security.PublicKey

data class Receiver(
    val publicKey: PublicKey,
    val fcmKey: String,
    val createdAt: Instant?,
    val updatedAt: Instant?,
)
