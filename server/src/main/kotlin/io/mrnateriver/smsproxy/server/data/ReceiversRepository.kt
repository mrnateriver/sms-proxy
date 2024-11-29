package io.mrnateriver.smsproxy.server.data

import io.mrnateriver.smsproxy.server.db.Receivers
import io.mrnateriver.smsproxy.server.db.ReceiversQueries
import io.mrnateriver.smsproxy.server.entities.Receiver
import kotlinx.datetime.toKotlinInstant
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.inject.Inject
import io.mrnateriver.smsproxy.server.data.contracts.ReceiversRepository as ReceiversRepositoryContract

class ReceiversRepository @Inject constructor(private val receiversQueries: ReceiversQueries) :
    ReceiversRepositoryContract {
    override suspend fun insert(key: String, publicKey: PublicKey, fcmKey: String): Receiver {
        return receiversQueries.insert(key, publicKey.encoded, fcmKey).executeAsOne().toReceiver()
    }

    override suspend fun findReceiverByKey(key: String): Receiver? {
        return receiversQueries.findReceiverByKey(key).executeAsOneOrNull()?.toReceiver()
    }

    override suspend fun doesReceiverExist(key: String): Boolean {
        return receiversQueries.doesReceiverExist(key).executeAsOne()
    }
}

private fun Receivers.toReceiver(): Receiver {
    val keySpec = X509EncodedKeySpec(publicKey)
    val keyFactory = KeyFactory.getInstance("RSA")
    val publicKey = keyFactory.generatePublic(keySpec)

    return Receiver(
        key = key,
        publicKey = publicKey,
        fcmKey = fcmKey,
        createdAt = createdAt.toInstant().toKotlinInstant(),
        updatedAt = updatedAt?.toInstant()?.toKotlinInstant(),
    )
}
