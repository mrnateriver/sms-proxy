package io.mrnateriver.smsproxy.server.usecases

import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.inject.Inject
import io.mrnateriver.smsproxy.server.data.contracts.ReceiversRepository as ReceiversRepositoryContract

class ReceiversUseCase @Inject constructor(private val receiversRepository: ReceiversRepositoryContract) {

    suspend fun registerReceiver(request: RegisterReceiverRequest) {
        // TODO: refactor to a better place
        // TODO: validate public key
        val keyData = Base64.getDecoder().decode(request.publicKey)
        val keySpec = X509EncodedKeySpec(keyData)
        val keyFactory = KeyFactory.getInstance("RSA")
        val publicKey = keyFactory.generatePublic(keySpec)

        receiversRepository.insert(request.receiverKey, publicKey, request.notificationsId)
    }

    data class RegisterReceiverRequest(
        val notificationsId: String,
        val receiverKey: String,
        val publicKey: String,
    )
}
