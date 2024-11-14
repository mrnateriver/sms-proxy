package io.mrnateriver.smsproxy.relay.services.framework

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageBackgroundProcessingService.MessageBackgroundProcessingResult.FAILURE
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageBackgroundProcessingService.MessageBackgroundProcessingResult.RETRY
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageBackgroundProcessingService.MessageBackgroundProcessingResult.SUCCESS
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageBackgroundProcessingService as MessageBackgroundProcessingServiceContract

@HiltWorker
class MessageProcessingWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val workerService: MessageBackgroundProcessingServiceContract,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        return when (workerService.handleUnprocessedMessages()) {
            SUCCESS -> Result.success()
            FAILURE -> Result.failure()
            RETRY -> Result.retry()
        }
    }
}
