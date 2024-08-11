import io.ktor.network.tls.certificates.saveToFile
import io.ktor.util.encodeBase64
import io.ktor.util.toCharArray
import org.bouncycastle.asn1.x500.RDN
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x500.style.BCStyle
import org.bouncycastle.asn1.x509.Extension
import org.bouncycastle.asn1.x509.GeneralName
import org.bouncycastle.asn1.x509.GeneralNames
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.cert.X509CertificateHolder
import org.bouncycastle.cert.X509v3CertificateBuilder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemWriter
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property
import java.io.File
import java.math.BigInteger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.MessageDigest
import java.security.cert.CertificateFactory
import java.time.Instant
import java.util.Date

typealias FilePath = String

enum class CertificateStorageFormat {
    PEM,
    JKS
}

open class GenerateCertificatesTask : DefaultTask() {
    init {
        group = "other"
        description =
            "Generates a self-signed certificate in the specified format and saves both it and it's public key at the specified paths."
    }

    @Input
    val applicationName = project.objects.property<String>()

    @Input
    @Optional
    val format: Property<CertificateStorageFormat> =
        project.objects.property<CertificateStorageFormat>()
            .convention(CertificateStorageFormat.PEM)

    @Input
    @Optional
    val organization: Property<String> = project.objects.property<String>()
        .convention(
            System.getenv("CERT_ORG")
                ?: (project.rootProject.extra["basePackageName"]?.toString() ?: "")
                    .split('.')
                    .dropLast(1)
                    .joinToString(".")
        )

    @Input
    @Optional
    val city: Property<String> =
        project.objects.property<String>().convention(System.getenv("CERT_CITY") ?: "")

    @Input
    @Optional
    val country: Property<String> =
        project.objects.property<String>().convention(System.getenv("CERT_COUNTRY") ?: "")

    @Input
    @Optional
    val keyAlias: Property<String> = project.objects.property<String>()
        .convention(System.getenv("CERT_KEY_ALIAS") ?: "privateKey")

    @Input
    @Optional
    val certPassword: Property<String> =
        project.objects.property<String>().convention(System.getenv("CERT_KEY_PASSWORD"))

    @Input
    @Optional
    val storePassword: Property<String> = project.objects.property<String>()
        .convention(System.getenv("CERT_KEY_STORE_PASSWORD"))

    @Input
    @Optional
    val validForDays: Property<Long> = project.objects.property<Long>().convention(365 * 10)

    @Input
    @Optional
    val keyLengthBits: Property<Int> = project.objects.property<Int>().convention(4096)

    @Input
    @Optional
    val outputKeyStoreFile: Property<FilePath> = project.objects.property<FilePath>()
        .convention("src/main/resources/proxy-api-server-certificate.jks")

    @Input
    @Optional
    val outputPrivateKeyFile: Property<FilePath> = project.objects.property<FilePath>()

    @Input
    @Optional
    val outputCertificatesFiles = project.objects.listProperty<FilePath>()

    @TaskAction
    fun run() {
        validateInputs()

        val keyPair = generateKeys()
        val cert = generateCertificate(keyPair)

        @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
        when (format.get()) {
            CertificateStorageFormat.PEM -> createPrivateKeyPem(keyPair)
            CertificateStorageFormat.JKS -> createJavaKeyStore(cert, keyPair)
        }

        createCertificatesPem(cert)
    }

    private fun createCertificatesPem(cert: X509CertificateHolder) {
        for (file in outputCertificatesFiles.get()) {
            val outputCertificateFile = File(file)
            outputCertificateFile.parentFile?.mkdirs()
            outputCertificateFile.writer().use {
                PemWriter(it).use { pem ->
                    pem.writeObject(PemObject("CERTIFICATE", cert.encoded))
                }
            }
        }
    }

    private fun createPrivateKeyPem(keyPair: KeyPair) {
        val outputPrivateKeyFile = project.file(outputPrivateKeyFile)
        outputPrivateKeyFile.parentFile?.mkdirs()
        outputPrivateKeyFile.writer().use {
            PemWriter(it).use { pem ->
                pem.writeObject(PemObject("PRIVATE KEY", keyPair.private.encoded))
            }
        }
    }

    private fun createJavaKeyStore(cert: X509CertificateHolder, keyPair: KeyPair) {
        val javaCert =
            CertificateFactory.getInstance("X.509").generateCertificate(cert.encoded.inputStream())

        KeyStore.getInstance(KeyStore.getDefaultType())?.apply {
            load(null, null)
            setKeyEntry(
                keyAlias.get(),
                keyPair.private,
                certPassword.get().toCharArray(),
                arrayOf(javaCert)
            )

            val outputKeyStoreFile = project.file(outputKeyStoreFile)
            saveToFile(outputKeyStoreFile, storePassword.get())
        }
    }

    private fun generateCertificate(keyPair: KeyPair): X509CertificateHolder {
        val subPubKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.public.encoded)

        val now = Instant.now()
        val validFrom = Date.from(now)
        val validTo = Date.from(now.plusSeconds(60L * 60 * 24 * validForDays.get()))

        val serverCN = applicationName.get()
        val validRdns = listOf(
            BCStyle.CN to serverCN,
            BCStyle.O to organization.get(),
            BCStyle.L to city.get(),
            BCStyle.C to country.get(),
        ).filter { it.second.isNotBlank() }
            .map { RDN(it.first, BCStyle.INSTANCE.stringToValue(it.first, it.second)) }
            .toTypedArray()

        val x500Name = X500Name(validRdns)
        val certBuilder = X509v3CertificateBuilder(
            x500Name,
            BigInteger.ONE,
            validFrom,
            validTo,
            x500Name,
            subPubKeyInfo
        )

        val generalNames = GeneralNames(arrayOf(GeneralName(GeneralName.dNSName, serverCN)))
        certBuilder.addExtension(Extension.subjectAlternativeName, false, generalNames)

        val signer = JcaContentSignerBuilder("SHA256WithRSA")
            .setProvider(BouncyCastleProvider())
            .build(keyPair.private)

        return certBuilder.build(signer)
    }

    private fun generateKeys(): KeyPair {
        return KeyPairGenerator.getInstance("RSA")
            .apply { initialize(keyLengthBits.get()) }
            .genKeyPair()
    }

    private fun KeyPair.publicKeySha256(): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(public.encoded).encodeBase64()
    }

    private fun validateInputs() {
        if (applicationName.get().isBlank()) {
            throw IllegalArgumentException("applicationName must not be blank")
        }

        if (validForDays.get() <= 0) {
            throw IllegalArgumentException("validForDays must be greater than 0")
        }

        if (format.get() == CertificateStorageFormat.JKS) {
            if (keyAlias.get().isBlank()) {
                throw IllegalArgumentException("keyAlias must not be blank when using JKS format")
            }
            if (certPassword.get().isBlank()) {
                throw IllegalArgumentException("certPassword must not be blank")
            }
            if (storePassword.get().isBlank()) {
                throw IllegalArgumentException("storePassword must not be blank")
            }
        }
    }
}