package cl.emilym.sinatra.ui.presentation.screens.preferences

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cl.emilym.sinatra.data.models.ContentLink
import cl.emilym.sinatra.data.models.DisclosureType
import cl.emilym.sinatra.data.repository.ContentRepository
import cl.emilym.sinatra.data.repository.PlatformContext
import cl.emilym.sinatra.data.repository.RecentVisitRepository
import cl.emilym.sinatra.ui.widgets.SinatraScreenModel
import cl.emilym.sinatra.ui.widgets.platformContext
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.core.annotation.Factory
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.preferences_location_title
import sinatra.ui.generated.resources.preferences_root_title
import sinatra.ui.generated.resources.preferences_routing_title
import sinatra.ui.generated.resources.preferences_setting_clear_recent_history

@Factory
class RootPreferencesViewModel(
    private val recentVisitRepository: RecentVisitRepository
): SinatraScreenModel {

    fun clearVisitHistory() {
        screenModelScope.launch {
            recentVisitRepository.clear()
        }
    }

}

class RootPreferencesScreen: PreferencesScreen() {
    override val key: ScreenKey = "preferences-root"

    override val title: String
        @Composable
        get() = stringResource(Res.string.preferences_root_title)

    @Composable
    override fun ColumnScope.Preferences(preferencesCollection: PreferencesCollection) {}

    @Composable
    override fun options(): List<ContentLink> {
        val context = platformContext()
        val viewModel = koinScreenModel<RootPreferencesViewModel>()

        return listOf(
            ContentLink.native(
                stringResource(Res.string.preferences_routing_title),
                ContentRepository.NATIVE_PREFERENCES_ROUTING_ID
            ),
            ContentLink.Custom(
                stringResource(Res.string.preferences_location_title),
                DisclosureType.EXTERNAL,
                0,
            ) {
                openLocationSettings(context)
            },
            ContentLink.Custom(
                stringResource(Res.string.preferences_setting_clear_recent_history),
                DisclosureType.NONE,
                0,
            ) {
                viewModel.clearVisitHistory()
            }
        )
    }

}

expect fun openLocationSettings(platformContext: PlatformContext)