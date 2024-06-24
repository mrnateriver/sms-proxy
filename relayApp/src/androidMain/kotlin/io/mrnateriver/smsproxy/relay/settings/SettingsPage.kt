package io.mrnateriver.smsproxy.relay.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import io.mrnateriver.smsproxy.relay.layout.AppContentSurface
import io.mrnateriver.smsproxy.shared.AppSpacings
import me.zhanghai.compose.preference.TextFieldPreference
import me.zhanghai.compose.preference.checkboxPreference
import me.zhanghai.compose.preference.rememberPreferenceState

private const val SettingsPageRoute = "settings"

fun NavGraphBuilder.settingsPage() {
    composable(SettingsPageRoute) {
        SettingsPage()
    }
}

fun NavController.navigateToSettingsPage(builder: (NavOptionsBuilder.() -> Unit)? = null) {
    navigate(SettingsPageRoute, if (builder == null) null else navOptions(builder))
}

fun isSettingsPageRoute(dest: NavDestination?): Boolean = dest?.route == SettingsPageRoute

private const val PREF_KEY_API_SERVER_ADDRESS = "api-server-address"
private const val PREF_KEY_API_SERVER_RECEIVER_KEY = "api-server-receiver-key"
private const val PREF_KEY_SHOW_RECENT_MESSAGES = "show-recent-messages"

@Preview
@Composable
fun SettingsPage() {
    AppContentSurface {
        val apiServerAddressValue = rememberPreferenceState(PREF_KEY_API_SERVER_ADDRESS, "")
        val apiServerReceiverKeyValue =
            rememberPreferenceState(PREF_KEY_API_SERVER_RECEIVER_KEY, "")

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                // TODO: refactor together with ServerSettingsStatus()
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppSpacings.medium),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ) {
                    Row(
                        modifier = Modifier.padding(AppSpacings.medium),
                        horizontalArrangement = Arrangement.spacedBy(AppSpacings.medium),
                    ) {
                        Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
                        // TODO: proper text + i18n
                        Text(text = "Server address and receiver key must be set for proxying messages.")
                    }
                }
            }

            item(key = PREF_KEY_API_SERVER_ADDRESS, contentType = "TextFieldPreference") {
                val value by apiServerAddressValue
                Box(
                    modifier = Modifier
//                        .background(MaterialTheme.colorScheme.errorContainer)
                        .fillMaxWidth()
                ) {
                    TextFieldPreference(
                        state = apiServerAddressValue,
                        textToValue = { it },
                        summary = {
                            Text(
                                value.ifEmpty { "No server address set" },
                                color = MaterialTheme.colorScheme.error,
                            )
                        },
                        title = {
                            Text(
                                "Server Address",
                            )
                        }, // TODO: i18n
//                        icon = {
//                            Icon(
//                                imageVector = Icons.Outlined.Share,
//                                contentDescription = null,
//                                tint = MaterialTheme.colorScheme.error,
//                            )
//                        },
                    )
                }

//                Surface(
//                    shape = MaterialTheme.shapes.large,
//                    color = MaterialTheme.colorScheme.errorContainer,
//                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                ) {
//                    Row(modifier = Modifier.padding(AppSpacings.small)) {
//                        Icon(
//                            imageVector = Icons.Outlined.Warning,
//                            contentDescription = null,
//                            modifier = Modifier.size(12.dp)
//                        )
//                        Text(
//                            "The server address is used to send SMS messages to the server. " +
//                                    "Make sure to set it correctly.",
//                            style = MaterialTheme.typography.bodySmall,
//                        )
//                    }
//                }

                HorizontalDivider()
            }

            item(key = PREF_KEY_API_SERVER_RECEIVER_KEY, contentType = "TextFieldPreference") {
                val value by apiServerReceiverKeyValue
                Box(
                    modifier = Modifier
//                        .background(MaterialTheme.colorScheme.errorContainer)
                        .fillMaxWidth()
                ) {
                    TextFieldPreference(
                        state = apiServerReceiverKeyValue,
                        textToValue = { it },
                        summary = {
                            Text(
                                value.ifEmpty { "No key set" },
                                color = MaterialTheme.colorScheme.error,
                            )
                        },
                        title = {
                            Text(
                                "Receiver Key",
//                                color = MaterialTheme.colorScheme.error
                            )
                        }, // TODO: i18n
//                        icon = {
//                            Icon(
//                                imageVector = Icons.Outlined.Lock,
//                                contentDescription = null,
//                                tint = MaterialTheme.colorScheme.error,
//                            )
//                        },
                    )
                }

                HorizontalDivider()
            }

            checkboxPreference(
                key = PREF_KEY_SHOW_RECENT_MESSAGES,
                defaultValue = true,
                title = { Text("Show Recent Messages") }, // TODO: i18n
//                icon = { Icon(imageVector = Icons.Outlined.Email, contentDescription = null) },
                summary = { Text("Show recent messages on the home screen") },
            )
        }
    }
}
