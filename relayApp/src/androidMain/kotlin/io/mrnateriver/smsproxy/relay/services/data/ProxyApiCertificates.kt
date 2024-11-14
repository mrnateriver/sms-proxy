package io.mrnateriver.smsproxy.relay.services.data

data class ProxyApiCertificates(
    val serverCertificatePem: String? = null,
    val clientCertificatePem: String? = null,
    val clientPrivateKeyPem: String? = null,
)
