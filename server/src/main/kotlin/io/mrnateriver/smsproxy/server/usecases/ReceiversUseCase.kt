package io.mrnateriver.smsproxy.server.usecases

import io.mrnateriver.smsproxy.server.entities.exceptions.ValidationException
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.inject.Inject
import io.mrnateriver.smsproxy.server.data.contracts.ReceiversRepository as ReceiversRepositoryContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

private const val MIN_RSA_KEY_SIZE_BYTES = 256

class ReceiversUseCase @Inject constructor(
    private val receiversRepository: ReceiversRepositoryContract,
    private val observabilityService: ObservabilityServiceContract,
) {
    suspend fun registerReceiver(request: RegisterReceiverRequest) {
        val publicKey = validateAndParsePublicKey(request.publicKey)

        receiversRepository.insert(request.receiverKey, publicKey, request.notificationsId)
    }

    suspend fun updateReceiverFcmKey(request: UpdateReceiverFcmKeyRequest) {
        if (request.notificationsId == null) {
            throw ValidationException(mapOf("notificationsId" to listOf("Notifications ID must be set")))
        }

        receiversRepository.updateReceiverFcmKey(request.receiverKey, request.notificationsId)
    }

    private suspend fun validateAndParsePublicKey(publicKeyBase64: String): PublicKey {
        return observabilityService.runSpan("ReceiversUseCase.validateAndParsePublicKey") {
            try {
                val keyData = Base64.getDecoder().decode(publicKeyBase64)

                if (keyData.isEmpty() || keyData.size < MIN_RSA_KEY_SIZE_BYTES) {
                    throw ValidationException(
                        mapOf("publicKey" to listOf("Public key must be at least $MIN_RSA_KEY_SIZE_BYTES bytes long")),
                    )
                }

                val keySpec = X509EncodedKeySpec(keyData)
                val keyFactory = KeyFactory.getInstance("RSA")

                keyFactory.generatePublic(keySpec)
            } catch (e: IllegalArgumentException) {
                throw ValidationException(mapOf("publicKey" to listOf("Public key must be base64-encoded")), e)
            } catch (e: Exception) {
                throw ValidationException(mapOf("publicKey" to listOf("Public key must be a valid RSA public key")), e)
            }
        }
    }

    data class RegisterReceiverRequest(
        val receiverKey: String,
        val publicKey: String,
        val notificationsId: String,
    )

    data class UpdateReceiverFcmKeyRequest(
        val receiverKey: String,
        val notificationsId: String?,
    )
}
