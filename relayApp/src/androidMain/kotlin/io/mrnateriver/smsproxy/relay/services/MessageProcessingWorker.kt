package io.mrnateriver.smsproxy.relay.services

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.mrnateriver.smsproxy.relay.services.MessageProcessingWorkerResult.FAILURE
import io.mrnateriver.smsproxy.relay.services.MessageProcessingWorkerResult.RETRY
import io.mrnateriver.smsproxy.relay.services.MessageProcessingWorkerResult.SUCCESS

@HiltWorker
class MessageProcessingWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val workerService: MessageProcessingWorkerService,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        return when (workerService.handleUnprocessedMessages()) {
            SUCCESS -> Result.success()
            FAILURE -> Result.failure()
            RETRY -> Result.retry()
        }
    }
}