package io.mrnateriver.smsproxy.relay.services.data

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.mrnateriver.smsproxy.shared.contracts.LogLevel
import io.mrnateriver.smsproxy.shared.services.ProxyApiClientFactory
import io.mrnateriver.smsproxy.shared.services.createProxyApiClient
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import javax.inject.Singleton
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

@Module
@InstallIn(SingletonComponent::class)
object ProxyApiModule {
    @Provides
    @Singleton
    fun providesApiCertificates(
        observabilityService: ObservabilityServiceContract,
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
                observabilityService,
            )

        return ProxyApiCertificates(serverCertificatePem, clientCertificatePem, clientPrivateKeyPem)
    }

    @Provides
    @Singleton
    fun providesProxyApiClientFactory(
        observabilityService: ObservabilityServiceContract,
        certificates: ProxyApiCertificates?,
    ): ProxyApiClientFactory {
        return ProxyApiClientFactory {
            createProxyApiClient(
                serverCertificatePem = certificates?.serverCertificatePem,
                clientCertificatePem = certificates?.clientCertificatePem,
                clientPrivateKeyPem = certificates?.clientPrivateKeyPem,
                observabilityService = observabilityService,
                baseApiUrl = it,
            )
        }
    }

    private fun readAssetFile(
        fileName: String,
        context: Context,
        observabilityService: ObservabilityServiceContract,
    ): String? {
        return try {
            val assetManager = context.assets
            val inputStream = assetManager.open(fileName)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))

            bufferedReader.use { it.readText() }
        } catch (e: IOException) {
            observabilityService.reportException(e)
            observabilityService.log(
                LogLevel.WARNING,
                "Failed to read TLS certificate from assets: $fileName",
            )

            null
        }
    }
}
