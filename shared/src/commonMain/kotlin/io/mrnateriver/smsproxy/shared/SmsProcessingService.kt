package io.mrnateriver.smsproxy.shared

// TODO: tests

class SmsProcessingService(
    private val repository: SmsRepository,
    private val relay: SmsRelayService,
    private val observability: ObservabilityService
) {
    // TODO: logging with observability service
    // TODO: instrumentation (OTEL)

    fun process(sms: SmsData): SmsEntry {
        val entry = repository.save(sms)
        processEntry(entry)
        return entry
    }

    fun handleUnprocessedMessages() {
        for (sms in repository.getAll(SmsRelayStatus.ERROR, SmsRelayStatus.PENDING)) {
            processEntry(sms)
        }
    }

    private fun processEntry(entry: SmsEntry) {
        // TODO: coroutines and stuff
        // TODO: logging
        try {
            repository.updateStatus(entry.guid, SmsRelayStatus.IN_PROGRESS)
            relay.relay(entry)
            repository.updateStatus(entry.guid, SmsRelayStatus.SUCCESS)
        } catch (e: Exception) {
            repository.updateStatus(entry.guid, SmsRelayStatus.ERROR)
        }
    }

}