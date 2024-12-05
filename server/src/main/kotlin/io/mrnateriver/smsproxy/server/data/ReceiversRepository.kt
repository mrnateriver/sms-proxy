package io.mrnateriver.smsproxy.server.data

import io.mrnateriver.smsproxy.server.db.Receivers
import io.mrnateriver.smsproxy.server.db.ReceiversQueries
import io.mrnateriver.smsproxy.server.entities.Receiver
import io.mrnateriver.smsproxy.server.framework.HashingSecret
import kotlinx.datetime.toKotlinInstant
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import io.mrnateriver.smsproxy.server.data.contracts.ReceiversRepository as ReceiversRepositoryContract

class ReceiversRepository @Inject constructor(
    private val receiversQueries: ReceiversQueries,
    @HashingSecret private val hashingSecret: String,
) :
    ReceiversRepositoryContract {
    override suspend fun insert(key: String, publicKey: PublicKey, fcmKey: String): Receiver {
        return receiversQueries.insert(hmacSha512(key), publicKey.encoded, fcmKey).executeAsOne().toReceiver()
    }

    override suspend fun findReceiverByKey(key: String): Receiver? {
        return receiversQueries.findReceiverByKey(hmacSha512(key)).executeAsOneOrNull()?.toReceiver()
    }

    override suspend fun doesReceiverExist(key: String): Boolean {
        return receiversQueries.doesReceiverExist(hmacSha512(key)).executeAsOne()
    }

    private fun hmacSha512(data: String): ByteArray {
        val mac = Mac.getInstance("HmacSHA512")
        val secretKey = SecretKeySpec(hashingSecret.toByteArray(), "HmacSHA512")
        mac.init(secretKey)

        return mac.doFinal(data.toByteArray())
    }
}

private fun Receivers.toReceiver(): Receiver {
    val keySpec = X509EncodedKeySpec(publicKey)
    val keyFactory = KeyFactory.getInstance("RSA")
    val publicKey = keyFactory.generatePublic(keySpec)

    return Receiver(
        publicKey = publicKey,
        fcmKey = fcmKey,
        createdAt = createdAt.toInstant().toKotlinInstant(),
        updatedAt = updatedAt?.toInstant()?.toKotlinInstant(),
    )
}
