import io.ktor.network.tls.certificates.saveToFile
import io.ktor.util.encodeBase64
import io.ktor.util.toCharArray
import io.ktor.utils.io.core.use
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.cert.X509CertificateHolder
import org.bouncycastle.cert.X509v3CertificateBuilder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemWriter
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import java.math.BigInteger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.MessageDigest
import java.security.cert.CertificateFactory
import java.time.Instant
import java.util.Date

typealias PublicKeySha256 = String

data class CertificateRequest(
    val applicationName: String,
    val organisation: String = System.getenv("CERT_ORG") ?: "",
    val city: String = System.getenv("CERT_CITY") ?: "",
    val country: String = System.getenv("CERT_COUNTRY") ?: "",
)

fun CertificateRequest.withProjectDefaults(rootProject: Project) = copy(
    organisation = organisation.ifBlank {
        (rootProject.extra["basePackageName"]?.toString() ?: "org")
            .split('.')
            .dropLast(1)
            .joinToString(".")
    },
    city = city.ifBlank { "Unknown" },
    country = country.ifBlank { "Unknown" },
)

fun Project.generateCertificateStore(
    destinationFilePath: String,
    csr: CertificateRequest,
    keyAlias: String,
    certPassword: String,
    storePassword: String,
    validForDays: Long = 365 * 10,
): PublicKeySha256 {
    val keyPair = generateKeys()
    val cert = generateCertificate(keyPair, csr.withProjectDefaults(rootProject), validForDays)
    val javaCert =
        CertificateFactory.getInstance("X.509").generateCertificate(cert.encoded.inputStream())

    KeyStore.getInstance(KeyStore.getDefaultType())?.apply {
        load(null, null)
        setKeyEntry(keyAlias, keyPair.private, certPassword.toCharArray(), arrayOf(javaCert))
        saveToFile(file(destinationFilePath), storePassword)
    }

    return keyPair.publicKeySha256()
}

fun Project.generateCertificate(
    csr: CertificateRequest,
    certificateFilePath: String = "src/androidMain/assets/proxy-api-client-certificate.pem",
    privateKeyFilePath: String = "src/androidMain/assets/proxy-api-client-certificate-private-key.pem",
    validForDays: Long = 365 * 10,
): PublicKeySha256 {
    val keyPair = generateKeys()
    val cert = generateCertificate(keyPair, csr.withProjectDefaults(rootProject), validForDays)

    val certificateFile = file(certificateFilePath)
    certificateFile.parentFile?.mkdirs()
    certificateFile.writer().use {
        PemWriter(it).use {
            it.writeObject(PemObject("CERTIFICATE", cert.encoded))
        }
    }

    val privateKeyFile = file(privateKeyFilePath)
    privateKeyFile.parentFile?.mkdirs()
    privateKeyFile.writer().use {
        PemWriter(it).use {
            it.writeObject(PemObject("PRIVATE KEY", keyPair.private.encoded))
        }
    }

    return keyPair.publicKeySha256()
}

fun Project.savePublicKeySha256InProject(
    projectName: String,
    destinationFilePath: String,
    publicKeySha256: PublicKeySha256,
) {
    rootProject.childProjects.get(projectName)
        ?.file(destinationFilePath)
        ?.writeText(publicKeySha256)
}

private fun generateCertificate(
    keyPair: KeyPair,
    csr: CertificateRequest,
    validForDays: Long = 365 * 10,
): X509CertificateHolder {
    val subPubKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.public.encoded)

    val now = Instant.now()
    val validFrom = Date.from(now)
    val validTo = Date.from(now.plusSeconds(60L * 60 * 24 * validForDays))

    val x500Name =
        X500Name("CN=${csr.applicationName},O=${csr.organisation},L=${csr.city},C=${csr.country}")
    val certBuilder = X509v3CertificateBuilder(
        x500Name,
        BigInteger.ONE,
        validFrom,
        validTo,
        x500Name,
        subPubKeyInfo
    )

    val signer = JcaContentSignerBuilder("SHA256WithRSA")
        .setProvider(BouncyCastleProvider())
        .build(keyPair.private)

    return certBuilder.build(signer)
}

private fun generateKeys(): KeyPair {
    return KeyPairGenerator.getInstance("RSA").apply { initialize(4096) }.genKeyPair()
}

private fun KeyPair.publicKeySha256(): PublicKeySha256 {
    val digest = MessageDigest.getInstance("SHA-256")
    return digest.digest(public.encoded).encodeBase64()
}
