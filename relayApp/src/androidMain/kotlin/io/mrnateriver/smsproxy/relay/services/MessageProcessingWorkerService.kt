package io.mrnateriver.smsproxy.relay.services

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import io.mrnateriver.smsproxy.shared.models.MessageRelayStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration
import java.util.logging.Level
import javax.inject.Inject
import javax.inject.Singleton
import io.mrnateriver.smsproxy.shared.contracts.MessageProcessingService as MessageProcessingServiceContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

enum class MessageProcessingWorkerResult {
    SUCCESS,
    FAILURE,
    RETRY,
}

interface MessageProcessingWorkerServiceContract {
    suspend fun handleUnprocessedMessages(): MessageProcessingWorkerResult
    fun scheduleBackgroundWork(context: Context)
}

@Singleton
class MessageProcessingWorkerService @Inject constructor(
    private val processingService: MessageProcessingServiceContract,
    private val statsService: MessageStatsServiceContract,
    private val observabilityService: ObservabilityServiceContract,
) : MessageProcessingWorkerServiceContract {
    override suspend fun handleUnprocessedMessages(): MessageProcessingWorkerResult =
        withContext(Dispatchers.IO) {
            observabilityService.runSpan("MessageProcessingWorkerService.doWork") {
                val results = processingService
                    .handleUnprocessedMessages()
                    .map { it.sendStatus }.toList()
                val result = when {
                    results.any { it == MessageRelayStatus.ERROR || it == MessageRelayStatus.IN_PROGRESS } -> MessageProcessingWorkerResult.RETRY
                    results.any { it == MessageRelayStatus.SUCCESS } -> MessageProcessingWorkerResult.SUCCESS // This would mean the rest have FAILURE status, and we don't want to retry them
                    results.isNotEmpty() -> MessageProcessingWorkerResult.FAILURE
                    else -> MessageProcessingWorkerResult.SUCCESS
                }

                for (i in 0 until results.count { it == MessageRelayStatus.ERROR }) {
                    statsService.incrementProcessingFailures()
                }

                statsService.triggerUpdate()

                observabilityService.log(Level.FINE, "Processed ${results.size} messages: $result")
                result
            }
        }


    override fun scheduleBackgroundWork(context: Context) {
        MessageProcessingWorkerService.scheduleBackgroundWork(context)
    }

    companion object {
        private val BASE_RETRY_DELAY = Duration.ofMillis(WorkRequest.DEFAULT_BACKOFF_DELAY_MILLIS)

        fun scheduleBackgroundWork(context: Context) {
            val request: WorkRequest =
                OneTimeWorkRequestBuilder<MessageProcessingWorker>()
                    .setInitialDelay(BASE_RETRY_DELAY)
                    .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, BASE_RETRY_DELAY)
                    .build()

            WorkManager.getInstance(context).enqueue(request)
        }
    }
}