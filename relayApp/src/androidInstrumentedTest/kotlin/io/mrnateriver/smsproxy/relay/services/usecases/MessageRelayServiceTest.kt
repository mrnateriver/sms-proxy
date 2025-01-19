package io.mrnateriver.smsproxy.relay.services.usecases

import arrow.core.left
import io.mrnateriver.smsproxy.models.MessageProxyRequest
import io.mrnateriver.smsproxy.shared.models.MessageData
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import io.mrnateriver.smsproxy.shared.models.MessageRelayStatus
import io.mrnateriver.smsproxy.shared.services.ProxyApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import okhttp3.ResponseBody
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.Response
import java.util.UUID
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.SettingsService as SettingsServiceContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract
import io.mrnateriver.smsproxy.shared.services.ProxyApiClientFactory as ProxyApiClientFactoryContract

class MessageRelayServiceTest {
    private val apiClient = mock<ProxyApi> {
        onBlocking { receiversUpdate(any(), any()) }.thenReturn(Response.success(null))
        onBlocking { receiversRegister(any()) }.thenReturn(Response.success(null))
        onBlocking { messagesProxy(any()) }.thenReturn(Response.success(null))
    }
    private val apiClientFactory = mock<ProxyApiClientFactoryContract> {
        on { create(any()) }.thenReturn(apiClient)
    }
    private val settingsService = mock<SettingsServiceContract> {
        onBlocking { receiverKey }.thenReturn(flow { emit("123") })
        onBlocking { baseApiUrl }.thenReturn(flow { emit("http://localhost") })
    }
    private val observabilityService =
        mock<ObservabilityServiceContract> {
            onBlocking<ObservabilityServiceContract, Any> {
                runSpan(any<String>(), any<Map<String, String>>(), any<suspend () -> Unit>())
            } doSuspendableAnswer { invocation ->
                invocation.getArgument<suspend () -> Any>(2)()
            }
        }

    private val subject =
        MessageRelayService(
            apiClientFactory,
            settingsService,
            observabilityService,
            CoroutineScope(Dispatchers.Unconfined),
        )

    private val now = Instant.fromEpochMilliseconds(1723996071981)

    @Test
    fun messageRelayService_shouldOnlyCreateApiClientOnce(): Unit = runBlocking {
        subject.relay(createTestMessageEntry())
        subject.relay(createTestMessageEntry())

        verify(apiClientFactory, times(1)).create(any<String>())
    }

    @Test
    fun messageRelayService_shouldRecreateApiClientIfBaseUrlChanges(): Unit = runBlocking {
        val urlsFlow = MutableStateFlow("http://localhost")
        whenever(settingsService.baseApiUrl).thenReturn(urlsFlow)

        subject.relay(createTestMessageEntry())

        verify(apiClientFactory).create("http://localhost")

        urlsFlow.emit("http://localhost:8080")

        verify(apiClientFactory).create("http://localhost:8080")
    }

    @Test
    fun messageRelayService_shouldNotRecreateApiClientIfReceiverKeyChanges(): Unit = runBlocking {
        val keysFlow = MutableStateFlow("test1234test1234")
        whenever(settingsService.receiverKey).thenReturn(keysFlow)

        subject.relay(createTestMessageEntry())

        keysFlow.emit("456")

        verify(apiClientFactory, times(1)).create(any<String>())
    }

    @Test
    fun messageRelayService_shouldRelayMessage(): Unit = runBlocking {
        val entry = createTestMessageEntry()
        subject.relay(entry)

        val messageData = entry.messageData.leftOrNull()!!
        verify(apiClient).messagesProxy(
            MessageProxyRequest(
                "123",
                messageData.sender,
                messageData.message,
                messageData.receivedAt,
            ),
        )
    }

    @Test
    fun messageRelayService_shouldThrowExceptionIfRelayFails(): Unit = runBlocking {
        whenever(apiClient.messagesProxy(any())).thenReturn(
            Response.error(
                500,
                ResponseBody.create(null, ""),
            ),
        )

        val entry = createTestMessageEntry()
        try {
            subject.relay(entry)
        } catch (e: Exception) {
            assert(e.message!!.contains("Failed to relay message"))
        }
    }

    private fun createTestMessageEntry(
        status: MessageRelayStatus = MessageRelayStatus.PENDING,
    ) =
        MessageEntry(
            UUID.randomUUID(),
            "123",
            status,
            0,
            null,
            MessageData("123", now, "Hello, World!").left(),
            now,
            now,
        )
}
