package io.mrnateriver.smsproxy.relay.services

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.mrnateriver.smsproxy.shared.MessageProcessingService
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService
import io.mrnateriver.smsproxy.shared.models.MessageRelayStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration
import java.util.logging.Level

@HiltWorker
class MessageProcessingWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val smsProcessingService: MessageProcessingService,
    private val observabilityService: ObservabilityService,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            observabilityService.runSpan("MessageProcessingWorker.doWork") {
                val results = smsProcessingService.handleUnprocessedMessages().toList()

                observabilityService.log(Level.FINE, "Processing ${results.size} messages:")
                for (msg in results) {
                    observabilityService.log(Level.FINE, "$msg")
                }

                val result = when {
                    results.any { it.sendStatus == MessageRelayStatus.ERROR || it.sendStatus == MessageRelayStatus.IN_PROGRESS } -> Result.retry()
                    results.any { it.sendStatus == MessageRelayStatus.SUCCESS } -> Result.success() // This would mean the rest have FAILURE status, and we don't want to retry them
                    results.isNotEmpty() -> Result.failure()
                    else -> Result.success()
                }

                // TODO: record errors in stats service

                observabilityService.log(Level.FINE, "Processed ${results.size} messages: $result")
                result
            }
        }
    }

    companion object {
        private val BASE_RETRY_DELAY = Duration.ofMillis(WorkRequest.DEFAULT_BACKOFF_DELAY_MILLIS)

        fun schedule(context: Context) {
            val request: WorkRequest =
                OneTimeWorkRequestBuilder<MessageProcessingWorker>()
                    .setInitialDelay(BASE_RETRY_DELAY)
                    .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, BASE_RETRY_DELAY)
                    .build()

            WorkManager.getInstance(context).enqueue(request)
        }

    }
}