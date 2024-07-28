package io.mrnateriver.smsproxy.relay

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.mrnateriver.smsproxy.relay.services.settings.SettingsService
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(val settingsService: SettingsService) : ViewModel() {
    val showApiKeyError = BuildConfig.API_KEY.isBlank()

    // TODO: !
}