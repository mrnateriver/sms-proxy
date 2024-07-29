package io.mrnateriver.smsproxy.relay.services

import io.mrnateriver.smsproxy.api.DefaultApi as MessageProxyApi

interface MessageRelayApiClientFactory {
    fun create(baseApiUrl: String?): MessageProxyApi
}