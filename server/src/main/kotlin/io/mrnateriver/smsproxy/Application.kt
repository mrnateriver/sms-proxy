package io.mrnateriver.smsproxy

import com.squareup.moshi.Json
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import okio.Buffer
import java.io.File
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import kotlin.random.Random

const val SERVER_HOST = "0.0.0.0"
const val SERVER_PORT = 4430

fun main() {
    val keyStorePassword = System.getenv("CERT_KEY_STORE_PASSWORD").toCharArray()
    val keyPassword = System.getenv("CERT_KEY_PASSWORD").toCharArray()
    val keyAlias = System.getenv("CERT_KEY_ALIAS") ?: "serverKey"

    val serverKeyStore = getServerKeyStore(keyStorePassword)
    val clientsKeyStore = createClientsKeyStore()

    val server = embeddedServer(
        Netty,
        applicationEngineEnvironment {
            module { messageProxyApi() }

            sslConnector(
                keyStore = serverKeyStore,
                keyAlias = keyAlias,
                privateKeyPassword = { keyPassword },
                keyStorePassword = { keyStorePassword },
            ) {
                host = SERVER_HOST
                port = SERVER_PORT
                trustStore = clientsKeyStore
            }
        },
    )

    server.start(wait = true)
}

fun Application.messageProxyApi() {
    routing {
        post<MessageProxyRequest>("/messages/proxy") {
            println("Request: $it")
            call.respondText("Ktor: hello", status = HttpStatusCode.NoContent)
        }

        val rnd = Random(seed = 42)
        get("/hello") {
            call.respondText("Ktor: ${rnd.nextInt()}")
        }
    }
}

private fun getServerKeyStore(keyStorePassword: CharArray): KeyStore {
    val stream = ::main.javaClass.getResource("/proxy-api-server-certificate.jks")

    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
    keyStore.load(stream?.openStream(), keyStorePassword)
    return keyStore
}

private fun createClientsKeyStore(): KeyStore {
    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
    keyStore.load(null, null)

    val dir = File(::main.javaClass.getResource("/clients")!!.file)
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
        val certificates = certificateFactory
            .generateCertificates(
                Buffer().writeUtf8(this).inputStream(),
            )

        return certificates.single() as X509Certificate
    } catch (nsee: NoSuchElementException) {
        throw IllegalArgumentException("failed to decode certificate", nsee)
    } catch (iae: IllegalArgumentException) {
        throw IllegalArgumentException("failed to decode certificate", iae)
    } catch (e: GeneralSecurityException) {
        throw IllegalArgumentException("failed to decode certificate", e)
    }
}

// TODO: codegen
data class MessageProxyRequest(
    /* Random key of the end receiver of the proxied message. */
    @Json(name = "receiverKey")
    val receiverKey: String,

    @Json(name = "sender")
    val sender: String,

    @Json(name = "message")
    val message: String,

    @Json(name = "receivedAt")
    val receivedAt: kotlinx.datetime.Instant,
)
