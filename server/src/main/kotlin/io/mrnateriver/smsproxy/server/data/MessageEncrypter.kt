package io.mrnateriver.smsproxy.server.data

import io.mrnateriver.smsproxy.shared.models.MessageData
import java.security.PublicKey
import javax.crypto.Cipher
import javax.inject.Inject
import io.mrnateriver.smsproxy.server.data.contracts.MessageEncrypter as MessageEncrypterContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

class MessageEncrypter @Inject constructor(private val observabilityService: ObservabilityServiceContract) :
    MessageEncrypterContract {
    override suspend fun encrypt(publicKey: PublicKey, message: MessageData): ByteArray {
        return observabilityService.runSpan("MessageEncrypter.encrypt") {
            // TODO: move to shared module and reuse the encryption configuration between server and receiverApp
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)

            cipher.doFinal(message.toByteArray())
        }
    }
}

private fun MessageData.toByteArray(): ByteArray {
    return "${receivedAt.toEpochMilliseconds()}:$sender:$message".toByteArray()
}
