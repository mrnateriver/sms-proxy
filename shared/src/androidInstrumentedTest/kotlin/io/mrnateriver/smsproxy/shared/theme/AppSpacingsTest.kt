package io.mrnateriver.smsproxy.shared.theme

import io.mrnateriver.smsproxy.shared.composables.theme.AppSpacings
import org.junit.Test

class AppSpacingsTest {
    @Test
    fun appSpacings_multiplesOfTheSmallestSpacing_shouldBeConsistent() {
        val tiny = AppSpacings.tiny.value.toInt()

        val spacings = listOf(
            AppSpacings.small,
            AppSpacings.medium,
            AppSpacings.large,
            AppSpacings.extraLarge,
        )

        for (i in 1..<spacings.size) {
            val prev = spacings[i - 1]
            val cur = spacings[i]
            assert((cur.value.toInt() - prev.value.toInt()) % tiny == 0) {
                "Spacing (${cur.value}) is not greater than the previous value (${prev.value}) " + //
                    "by a multiple of AppSpacings.tiny ($tiny)"
            }
        }
    }
}
