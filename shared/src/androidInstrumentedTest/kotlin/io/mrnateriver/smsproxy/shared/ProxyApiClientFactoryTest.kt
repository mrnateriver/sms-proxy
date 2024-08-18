package io.mrnateriver.smsproxy.shared

import okhttp3.tls.certificatePem
import okhttp3.tls.decodeCertificatePem
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.util.Base64
import javax.net.ssl.SSLSession

class ProxyApiClientFactoryTest {

    @Test
    fun createHandshakeCertificates_shouldDecodeServerPem() {
        val certs = createHandshakeCertificates(
            serverCertificatePem = SERVER_CERTIFICATE_PEM,
            clientCertificatePem = CLIENT_CERTIFICATE_PEM,
            clientPrivateKeyPem = CLIENT_KEY_PEM,
        )

        val accepted = certs.trustManager.acceptedIssuers

        assertTrue(accepted.size == 1)
        assertTrue(accepted.first().certificatePem().trim() == SERVER_CERTIFICATE_PEM)
    }

    @Test
    fun createHandshakeCertificates_shouldDecodeClientPem() {
        val certs = createHandshakeCertificates(
            serverCertificatePem = SERVER_CERTIFICATE_PEM,
            clientCertificatePem = CLIENT_CERTIFICATE_PEM,
            clientPrivateKeyPem = CLIENT_KEY_PEM,
        )

        val accepted = certs.keyManager.getCertificateChain("private")

        assertTrue(accepted.size == 1)
        assertTrue(accepted.first().certificatePem().trim() == CLIENT_CERTIFICATE_PEM)

        val privateKey = certs.keyManager.getPrivateKey("private")

        assertTrue(privateKey != null)

        val recodedKey = Base64.getEncoder().encodeToString(privateKey.encoded)
        val recodedPrivateKey = "-----BEGINPRIVATEKEY-----$recodedKey-----ENDPRIVATEKEY-----"

        assertTrue(recodedPrivateKey == CLIENT_KEY_PEM.filterNot { it.isWhitespace() })
    }

    @Test
    fun createHandshakeCertificates_shouldSkipServerIfNotSet() {
        val certs = createHandshakeCertificates(
            serverCertificatePem = null,
            clientCertificatePem = CLIENT_CERTIFICATE_PEM,
            clientPrivateKeyPem = CLIENT_KEY_PEM,
        )

        val accepted = certs.trustManager.acceptedIssuers

        assertTrue(accepted.isEmpty())
    }

    @Test
    fun createHandshakeCertificates_shouldSkipClientIfNotSet() {
        val certs = createHandshakeCertificates(
            serverCertificatePem = SERVER_CERTIFICATE_PEM,
            clientCertificatePem = null,
            clientPrivateKeyPem = null,
        )

        val accepted = certs.keyManager.getCertificateChain("private")
        assertTrue(accepted.isNullOrEmpty())

        val privateKey = certs.keyManager.getPrivateKey("private")
        assertTrue(privateKey == null)
    }

    @Test
    fun verifySelfSignedCertificateHost_shouldVerifyServerCN() {
        val mockSession = mock<SSLSession> {
            on { peerCertificates } doReturn arrayOf(SERVER_CERTIFICATE_PEM.decodeCertificatePem())
        }

        assertTrue(verifySelfSignedCertificateHost(BuildConfig.API_SERVER_CN, mockSession))
    }

    @Test
    fun verifySelfSignedCertificateHost_shouldNotVerifyAnythingOtherThanServerCN() {
        val mockSession = mock<SSLSession> {
            on { peerCertificates } doReturn arrayOf(UNVERIFIED_SERVER_CERTIFICATE_PEM.decodeCertificatePem())
        }

        assertFalse(verifySelfSignedCertificateHost("whatever", mockSession))
    }
}

private const val CLIENT_KEY_PEM = """-----BEGIN PRIVATE KEY-----
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
0XavOv8ird0+h2Mt//lmHLJ0lQxpiSH1
-----END PRIVATE KEY-----"""

private const val CLIENT_CERTIFICATE_PEM = """-----BEGIN CERTIFICATE-----
MIIE0TCCArmgAwIBAgIBATANBgkqhkiG9w0BAQsFADAsMREwDwYDVQQDDAhyZWxh
eUFwcDEXMBUGA1UECgwOaW8ubXJuYXRlcml2ZXIwHhcNMjQwODExMTgxNjEzWhcN
MzQwODA5MTgxNjEzWjAsMREwDwYDVQQDDAhyZWxheUFwcDEXMBUGA1UECgwOaW8u
bXJuYXRlcml2ZXIwggIiMA0GCSqGSIb3DQEBAQUAA4ICDwAwggIKAoICAQC570df
+PqrHujlr1WhAOepwh+BtYX/ZxpSM2ZP+C0rtnE9hvpOaT+bBpYhIuFwmAUkq/DG
hfp+mJxU+EEASDWzAKAtwJErhPpGj4kISNG24AZw/KBNgHhzPfAQyAa2F6D3vTzV
8D9vgGaPEZfluroAn8E2qFzxoL4OJrEbgNSo9Bp1+mOMlsk44yu/TGvYHJFFJW1k
Mvj9dOyoeg4tMH28QYkDvn3RfYeuW21LkREUVY0MM+d9ga+jJkTq43WaIoPf7SNL
GOhirfvxFW/GMzkVi2OkDsH3NN/pMIcdSX8eVIoBJpybC9tWpZ4UYqxEd3wD7L4B
Bv8DpWxcd9SFQ9F3SdrlUd4fy56U7rvvCx2wE3hqchneJIIRt6C27JLKzYV3EeRa
5FhxDBnKh0MzWidazb5uimyYGpdP5qv/ZcecbNPMUu571X85xxY3YZlxKC8vUau4
0MwyNIdKDYpVvhO/tUWuWhw3YQZs2M+gImeeJv0emAhgZxNWNhUTLIH1DOunqGGb
eDWVkWCModgRzAim2j5nPdBv8Ba7RAEsoGpcnauUSswjihkQZFfQmECPPqJ4rq3b
yUR/jfeoDRenM6eQ5y2My7p96YklT1gMy01hiDPOMnt25Oyxw3A+ISn96o0bDpro
5aSQev6qiMB2T3vt3PcY4lshs01fbGR9lrFRBQIDAQABMA0GCSqGSIb3DQEBCwUA
A4ICAQCHJ9V3WtJ2mZT2fdMtjsOW+sofcMf1adAvS6EaZv53D38qqVLmZS1itNL6
hDRZkZCEVXQOvKmConVswfN8Hzz+PPYhN6xHhwXeztB9x1nCw0fauaifGCVfzHyv
lw0P8I3F3YTSYhQqk0e+10wGes5uIPWh3j7rSSCydWU4O0YwRgLwMUIkAL77MUaa
Gb9XIZg4TNDWE71CPGEFQKPARA0S+Ao/UDwcJFv26g9c7GFnh1bXY3HofHjxkISE
5w24NDoSDu+KXqaMERiQdk83ie5wYj/f4WoLqnQDC/oAY3XHQ11UZGIaJZFsmK9z
PfYPnLSQlXZve1mlmLmY3Bv7H2pko3suC3xdQGwxnvKA+nOXMhXA9Pfl4DTzjbgb
/mbmAdyB24AQD2wj/Y6redx5QSHubJe2D5oG62loIlvADVfRRE22AEVxDA5ZJaF5
ZSxi6sRk4zUti1KEibaAG1bRMmSCLQVg7CugAl/LKvKFkllgCZZNfNgQbIHk4RUV
Yo+LjYfMdWBB8dY719+wWnVOdsiUFf0t6ILDd1evQCOgO1AWvmGmvKrOmjbSV8Vx
adRv9s3oPTYdNsEvPhKQBLygzBeFxs1hp94hvXDEvTOEaIjMmCO0buzmHh1cXL6G
w/xOWowg1jV2C9HsoWB/NVx3Hr56JiUiOhO39vxYbHB/oinIAw==
-----END CERTIFICATE-----"""

private const val SERVER_CERTIFICATE_PEM = """-----BEGIN CERTIFICATE-----
MIIFKjCCAxKgAwIBAgIBATANBgkqhkiG9w0BAQsFADBMMREwDwYDVQQDDAhwcm94
eUFwaTEXMBUGA1UECgwObXJuYXRlcml2ZXIuaW8xDTALBgNVBAcMBE9zbG8xDzAN
BgNVBAYTBk5vcndheTAeFw0yNDA4MTExOTUzNDlaFw0zNDA4MDkxOTUzNDlaMEwx
ETAPBgNVBAMMCHByb3h5QXBpMRcwFQYDVQQKDA5tcm5hdGVyaXZlci5pbzENMAsG
A1UEBwwET3NsbzEPMA0GA1UEBhMGTm9yd2F5MIICIjANBgkqhkiG9w0BAQEFAAOC
Ag8AMIICCgKCAgEA8nSF5LHQqWgMUFiWOl4afKRNLg2GS4Q30xn2mkgUr/YuiB9s
jp3uN28L1Njbnz2EPfm9hvWvyYZI9+QB8T+/MU+yvoiCkHS0e0P7zB+CdW6hUrKO
UirI1JxqkcYwbIFJFwUQ9V3cx9+/lRN+ZU/S0pxYXGR5oJjgpqxliPV5IzEkeNsC
gwyknfLgKwgrFAnLuxpQceXVKM6VbErL+wbCFQDUwjCi9EtDs1azoj2vcvj9maCZ
HUS5tYlFGlvFwmXewNArhRVF8LByoR2SiMrgmh4/1C6gWfMgNiazS/GSpPhR9s2I
d4MVLk5CZ8nh+jVP3/3HkbGY9wrud9vuP282r88RNr2vKvK29eYo1A9uNYpIuml0
cU/NTLKQlJ8B1bqZpNor8k5Xr90JHHYWJalOzYoYfVjoAA1l9sCme0tg6ktzUmXp
66K9yf7BXp9vwORxJ0lI2hndH4NP4TedVl5TtTZkLNxug83Z92EMQhcf7dPOxwrz
6ppEaQpGXlnshpneQp+AF9zbHp8aN3gd1nmkrjc09AotrP+Vj2C6cq88KnZyTM5P
EgHaQ8wLt1cmQmfo0L6UAyVEaQlPgc0FYXhdiLz0HaqSHG6bCeotrHWwyVSfaLrX
tIvOP5r8YTyuUd3622OaCuaxclyRbKkunGUF9gZJSEf4T4KCCZzmt5ZtXaECAwEA
AaMXMBUwEwYDVR0RBAwwCoIIcHJveHlBcGkwDQYJKoZIhvcNAQELBQADggIBAMEH
kxVmTsxP3mMwvepKJHMQs+JBG2/Ty/RWuhaAewAcRmGyiu8E4QNV8SlnDLJSYM87
yV1jiWbtHxX9LFRa2Tmnc9yxgVSp6QrrRxAb+6973+5mnr3s1T6cYMN7KN+p355A
9a8L4xxUtfoHjbDaxUjdS0faXXVAD/j063vkyHMB5znihWS3uRioye3TqBpSnLYv
aMtQzo+W2PWP1VoR4WNAG3b9KBIz7nb2MHSqfELO+G3CdRCWJIek1U3X2ARl+kNy
ZlesWIL1OevKJm8fDgJ23lVaq6IQHYMbhsgbxxK+0LCZIpiwsjrCx+y9fmMJJ/L7
UQTRS4FNC/oNCsjQgi0XPRS/Z1mmkibTJswuPn60Nyb6FH1xJP1uIez5pPLpnlgX
rebRFy4+0UUKFwiAwLTxqxNXl6V9uQyaXUnC2bMGalQMc33o/DGHJn7gpQYjdasX
4hiE5sERqZovPteaZULfpnN3HcVn7MOoAGI4UW6MNjpXUNLPTpwYSuVMA9ewbqwC
hp1CzUw2cUPqi/I97xXfI27/sUIWDpRYLvpiNNWUaXxDi+SsdO4yYUsAErdRQoCb
hAqamrOvdPuOv93o/L3GAVlnSpEQQr/MqxKtz0aYbQkU8atznZqlgbO1VG/4qqAE
WFnNhvFcmao5h5aWoze+WZl454eHfFzS5ewym4c4
-----END CERTIFICATE-----"""

private const val UNVERIFIED_SERVER_CERTIFICATE_PEM = """-----BEGIN CERTIFICATE-----
MIIFKjCCAxKgAwIBAgIBATANBgkqhkiG9w0BAQsFADBMMREwDwYDVQQDDAh3aGF0
ZXZlcjEXMBUGA1UECgwObXJuYXRlcml2ZXIuaW8xDTALBgNVBAcMBE9zbG8xDzAN
BgNVBAYTBk5vcndheTAeFw0yNDA4MTgxNDA4MjFaFw0zNDA4MTYxNDA4MjFaMEwx
ETAPBgNVBAMMCHdoYXRldmVyMRcwFQYDVQQKDA5tcm5hdGVyaXZlci5pbzENMAsG
A1UEBwwET3NsbzEPMA0GA1UEBhMGTm9yd2F5MIICIjANBgkqhkiG9w0BAQEFAAOC
Ag8AMIICCgKCAgEAxnLU/mrF+NN9l9aju4pck40uQr3vsu0BwnXI3XF1FoOs8Fa6
k6G1qjrPHdnYFA3HSHv2RxBFlVdbcxXXlfU7uVQyHpextFoaXQi9KEHPIe6oO7D4
eqdLn252P8cUU1Bu5apGWO1O0+nS4Fy72uQOaJvNoBOrA+Y1yPa+yDoWTCUWlvr7
058JHyNDzgpn7P1aBNLoquk5F7GVnsMaOk+Y5V6MKWAdJU8Cgiyd7ROpI8PJddo2
ysOcw7BaR1AXrX6V7KGjDvUTsShBLfpqECWWFwNBeTyTjPOeKUYhxIEMv4TnX01T
zEUjCC0unS+oTn6xV6fh+IsLktifnkT0lGAbbwsNYWtEp3HYPoImJ4AJC+Sc1ZoK
PYA8VLpBGlVxNMH6JhKFy3eSM1p5jSdwOa4bVVSKtScP/OlZ45XCtOGTcec+kNtE
LbYVGaxgZ3pV/k89yLwMOdNB296lvYHdXz11kEj7aZ7gapWa5DkaW0JGK2EgAXR7
sxiU2AAJ4+/vsQx7k7a7gnjinw31uC2EVgkf5735gpPjxyWjah4lY8SE47bWR6x5
kf0PQkeOfxfpTFTKz/uS/VXQD7NEJI+B0W4jCZf6MPo8OF2hD4G6C/5F2v+G1cam
Q/r+9OaK3d2BDCkcwQtf0W4mwYr9ya8gvMzAnDXTw/x01GuEkUw8UGYVzokCAwEA
AaMXMBUwEwYDVR0RBAwwCoIId2hhdGV2ZXIwDQYJKoZIhvcNAQELBQADggIBAB6X
sHpGDymx4+odxpi0mn2s8mphttoMJEJapMa3bCyZeMf8yyRf3BsOgZF7lexFG5/6
r4L9s0NT8UdyL3SfzcEYaBYwRFglKvkU/l5BelKF+fQnTyYMOYwhaz/Mqs6E5ihk
45ePV8lNtInMkLctbBidmz9llamq/QsRosDct0x0dVY4GPQxHsQ3RX4XQZoShOFg
Ln8aUCi2lxx0rn4K0m56Z6JW9M1So0Ot3jD5pKBEtCXL+lmWFoiitM80mqjU25yn
3jX7spziZfvr8001kJj1e1zs22u6rLyfGchpzrojWvBYRK/zOrr8m38b2Gdr7bvW
cjtj8o1O4aD+crskWYDHT7Jg8V/e3IhRg8TTFEItyZboX0jDIlKp2NiT5SVHqix2
jDCMBzX3cIZaPvFUnTr0sSYuDSJZN4R5b9iI8VU9iFbqurlX7Nmwt6cC1anI8GGA
/4rmwQK452YM3QisbTG2inyHWvayr3yAhXuUnAdQR6tybT2he/VYF5xdJGl06mEg
btTBQe/SFmp9AErz8xSEQWELE/3B6btmOadtc7ubhFDH+e6UFa6vpXnm/xxUCgcn
LdNzM18XcMRXPFF0XJ38OM0WNVw5jUj0STzlgQhtmyFrvvHNlGivqQy+LCMPpdk6
yFSZmmzLZdACn9e8qVvceBzKs1rIOPb9twAOAHuu
-----END CERTIFICATE-----"""
