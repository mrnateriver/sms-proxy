package io.mrnateriver.smsproxy.shared

import java.util.logging.Level

interface ObservabilityService {
    fun log(level: Level, message: String)
}