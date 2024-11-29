package io.mrnateriver.smsproxy.server.data.contracts

import io.mrnateriver.smsproxy.shared.models.MessageData
import java.security.PublicKey

interface MessageEncrypter {
    suspend fun encrypt(publicKey: PublicKey, message: MessageData): ByteArray
}
