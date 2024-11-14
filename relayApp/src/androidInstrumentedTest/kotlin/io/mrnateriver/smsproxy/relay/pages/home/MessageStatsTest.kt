package io.mrnateriver.smsproxy.relay.pages.home

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onSiblings
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.relay.services.usecases.models.MessageStatsData
import io.mrnateriver.smsproxy.relay.services.usecases.models.MessageStatsEntry
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class MessageStatsTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private val testDate = LocalDateTime(2021, 8, 23, 17, 56, 42)

    @Before
    fun prepare() {
        rule.setContent {
            MessageStats(
                data = MessageStatsData(
                    processed = MessageStatsEntry(123456, testDate),
                    relayed = MessageStatsEntry(123456, testDate),
                    errors = MessageStatsEntry(123456, testDate),
                    failures = MessageStatsEntry(123456, testDate),
                ),
            )
        }
    }

    @Test
    fun messageStats_showsTitle() {
        rule.onNodeWithText(rule.activity.getString(R.string.dashboard_stats_title)).assertExists()
    }

    @Test
    fun messageStats_showsProcessedCount() {
        assertStatsCard(R.string.dashboard_stats_card_title_processed)
    }

    @Test
    fun messageStats_showsRelayedCount() {
        assertStatsCard(R.string.dashboard_stats_card_title_relayed)
    }

    @Test
    fun messageStats_showsErrorsCount() {
        assertStatsCard(R.string.dashboard_stats_card_title_errors)
    }

    @Test
    fun messageStats_showsFailuresCount() {
        assertStatsCard(R.string.dashboard_stats_card_title_failures)
    }

    private fun assertStatsCard(@StringRes cardTitleStringId: Int) {
        val titleSiblings =
            rule.onNodeWithText(rule.activity.getString(cardTitleStringId))
                .assertExists()
                .onSiblings()

        titleSiblings
            .filterToOne(hasTestTag("entry-value"))
            .assertExists()
            .assertTextContains("123456")

        titleSiblings
            .filterToOne(hasTestTag("entry-date"))
            .assertExists()
            .assertTextContains(
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                    .format(testDate.toJavaLocalDateTime()),
            )
    }
}
