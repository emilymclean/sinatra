package cl.emilym.sinatra.data.repository

import org.koin.core.annotation.Factory

@Factory
class SettingsRepository(
    private val settingsPersistence: SettingsPersistence
) {
    
}