package io.mrnateriver.smsproxy.server

import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.sslConnector
import io.ktor.utils.io.streams.inputStream
import kotlinx.io.Buffer
import kotlinx.io.writeString
import java.io.File
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

fun ApplicationEngine.Configuration.tlsConnector(server: ServerConfiguration, config: TlsConfiguration) {
    val keyStorePassword = config.keyStorePassword.toCharArray()
    val keyPassword = config.keyPassword.toCharArray()
    val keyAlias = config.keyAlias

    val keyPath = config.keyPath
    val clientsKeysPath = config.clientsKeysPath

    val serverKeyStore = getServerKeyStore(keyPath, keyStorePassword)
    val clientsKeyStore = createClientsKeyStore(clientsKeysPath)

    sslConnector(
        keyStore = serverKeyStore,
        keyAlias = keyAlias,
        privateKeyPassword = { keyPassword },
        keyStorePassword = { keyStorePassword },
    ) {
        host = server.host
        port = server.port
        trustStore = clientsKeyStore
    }
}

private fun getServerKeyStore(keyStorePath: String, keyStorePassword: CharArray): KeyStore {
    val stream = File(keyStorePath).inputStream()

    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
    keyStore.load(stream, keyStorePassword)
    return keyStore
}

private fun createClientsKeyStore(clientsKeysPath: String): KeyStore {
    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
    keyStore.load(null, null)

    val dir = File(clientsKeysPath)
    dir.walk().forEach {
        if (it.isFile && it.name.endsWith(".pem")) {
            keyStore.setCertificateEntry(it.name, it.readText().decodeCertificatePem())
        }
    }

    return keyStore
}

@Suppress("ThrowsCount")
private fun String.decodeCertificatePem(): X509Certificate {
    try {
        val certificateFactory = CertificateFactory.getInstance("X.509")

        val buffer = Buffer()
        buffer.writeString(this)
        val certificates = certificateFactory.generateCertificates(buffer.inputStream())

        return certificates.single() as X509Certificate
    } catch (nsee: NoSuchElementException) {
        throw IllegalArgumentException("failed to decode certificate", nsee)
    } catch (iae: IllegalArgumentException) {
        throw IllegalArgumentException("failed to decode certificate", iae)
    } catch (e: GeneralSecurityException) {
        throw IllegalArgumentException("failed to decode certificate", e)
    }
}
