package io.mrnateriver.smsproxy.relay.services.usecases.contracts

interface MessageProcessingScheduler {
    fun scheduleBackgroundMessageProcessing()
}
