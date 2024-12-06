package io.mrnateriver.smsproxy.server.data.contracts

import io.mrnateriver.smsproxy.server.entities.Receiver
import java.security.PublicKey

interface ReceiversRepository {
    suspend fun insert(key: String, publicKey: PublicKey, fcmKey: String): Receiver
    suspend fun updateReceiverFcmKey(key: String, fcmKey: String): Receiver?
    suspend fun findReceiverByKey(key: String): Receiver?
    suspend fun doesReceiverExist(key: String): Boolean
}
