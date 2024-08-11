package io.mrnateriver.smsproxy.relay.services

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.mrnateriver.smsproxy.shared.ProxyApiClientFactory
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService
import io.mrnateriver.smsproxy.shared.createProxyApiClient
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.logging.Level

data class ProxyApiCertificates(
    val serverCertificatePem: String?,
    val clientCertificatePem: String?,
    val clientPrivateKeyPem: String?,
)

@Module
@InstallIn(SingletonComponent::class)
class ProxyApiModule {
    @Provides
    fun providesApiCertificates(
        observabilityService: ObservabilityService,
        @ApplicationContext context: Context,
    ): ProxyApiCertificates {
        val serverCertificatePem =
            readAssetFile("proxy-api-server-certificate.pem", context, observabilityService)
        val clientCertificatePem =
            readAssetFile("proxy-api-client-certificate.pem", context, observabilityService)
        val clientPrivateKeyPem =
            readAssetFile(
                "proxy-api-client-certificate-private-key.pem",
                context,
                observabilityService
            )

        return ProxyApiCertificates(serverCertificatePem, clientCertificatePem, clientPrivateKeyPem)
    }

    @Provides
    fun providesProxyApiClientFactory(
        observabilityService: ObservabilityService,
        certificates: ProxyApiCertificates?,
    ): ProxyApiClientFactory {

        return ProxyApiClientFactory {
            createProxyApiClient(
                serverCertificatePem = certificates?.serverCertificatePem,
                clientCertificatePem = certificates?.clientCertificatePem,
                clientPrivateKeyPem = certificates?.clientPrivateKeyPem,
                observabilityService = observabilityService,
                baseApiUrl = it
            )
        }
    }

    private fun readAssetFile(
        fileName: String,
        context: Context,
        observabilityService: ObservabilityService,
    ): String? {
        return try {
            val assetManager = context.assets
            val inputStream = assetManager.open(fileName)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))

            bufferedReader.use { it.readText() }

        } catch (e: Exception) {
            observabilityService.reportException(e)
            observabilityService.log(
                Level.WARNING,
                "Failed to read TLS certificate from assets: $fileName"
            )

            null
        }
    }
}