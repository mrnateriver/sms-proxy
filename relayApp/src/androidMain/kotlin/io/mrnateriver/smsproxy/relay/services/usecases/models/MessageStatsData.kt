package io.mrnateriver.smsproxy.relay.services.usecases.models

data class MessageStatsData(
    val processed: MessageStatsEntry = MessageStatsEntry(),
    val relayed: MessageStatsEntry = MessageStatsEntry(),
    val errors: MessageStatsEntry = MessageStatsEntry(),
    val failures: MessageStatsEntry = MessageStatsEntry(),
)
