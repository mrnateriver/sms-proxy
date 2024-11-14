package io.mrnateriver.smsproxy.relay.services.framework

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import javax.inject.Inject
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageProcessingScheduler as MessageProcessingSchedulerContract

class MessageProcessingScheduler @Inject constructor(@ApplicationContext private val context: Context) :
    MessageProcessingSchedulerContract {

    override fun scheduleBackgroundMessageProcessing() = scheduleBackgroundWork(context)

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
