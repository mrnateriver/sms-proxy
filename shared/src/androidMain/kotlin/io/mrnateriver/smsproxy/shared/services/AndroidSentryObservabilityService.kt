package io.mrnateriver.smsproxy.shared.services

import io.sentry.Sentry
import io.sentry.SpanStatus
import io.sentry.TransactionOptions

class AndroidSentryObservabilityService : AndroidObservabilityService() {
    override fun reportException(exception: Throwable) {
        Sentry.captureException(exception)
        super.reportException(exception)
    }

    override suspend fun <T> runSpan(name: String, attrs: Map<String, String>, body: suspend () -> T): T {
        val scopeSpan = Sentry.getSpan()
        val tx = if (scopeSpan == null) {
            val txOptions = TransactionOptions()
            txOptions.isBindToScope = true

            Sentry.startTransaction(name, "runSpan", txOptions)
        } else {
            scopeSpan.startChild(name)
        }

        for ((key, value) in attrs) {
            tx.setData(key, value)
        }

        try {
            val result = body()
            return result
        } catch (e: Throwable) {
            tx.throwable = e
            tx.status = SpanStatus.INTERNAL_ERROR
            throw e
        } finally {
            tx.finish()
        }
    }

    // TODO: report metrics to a remote service, preferably Sentry if/when it supports it
}
