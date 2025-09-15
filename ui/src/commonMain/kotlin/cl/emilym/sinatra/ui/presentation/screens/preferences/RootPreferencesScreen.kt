package cl.emilym.sinatra.ui.presentation.screens.preferences

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.ContentLink
import cl.emilym.sinatra.data.models.DisclosureType
import cl.emilym.sinatra.data.repository.ContentRepository
import cl.emilym.sinatra.data.repository.PlatformContext
import cl.emilym.sinatra.data.repository.RecentVisitRepository
import cl.emilym.sinatra.domain.CacheInvalidationUseCase
import cl.emilym.sinatra.ui.widgets.ContentLinkColumn
import cl.emilym.sinatra.ui.widgets.ContentLinkWidget
import cl.emilym.sinatra.ui.widgets.SinatraScreenModel
import cl.emilym.sinatra.ui.widgets.platformContext
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.core.annotation.Factory
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.preferences_location_title
import sinatra.ui.generated.resources.preferences_root_title
import sinatra.ui.generated.resources.preferences_routing_title
import sinatra.ui.generated.resources.preferences_setting_clear_cache
import sinatra.ui.generated.resources.preferences_units_title
import sinatra.ui.generated.resources.preferences_setting_clear_recent_history

@Factory
class RootPreferencesViewModel(
    private val recentVisitRepository: RecentVisitRepository,
    private val cacheInvalidationUseCase: CacheInvalidationUseCase
): SinatraScreenModel {

    fun clearVisitHistory() {
        screenModelScope.launch {
            recentVisitRepository.clear()
        }
    }

    fun clearCache() {
        screenModelScope.launch {
            cacheInvalidationUseCase()
        }
    }

}

class RootPreferencesScreen: PreferencesScreen() {
    override val key: ScreenKey = "preferences-root"

    override val title: String
        @Composable
        get() = stringResource(Res.string.preferences_root_title)

    @Composable
    override fun ColumnScope.Preferences() {}

    @Composable
    override fun ColumnScope.BottomContent() {
        val context = platformContext()
        val viewModel = koinScreenModel<RootPreferencesViewModel>()

        Column {
            ContentLinkColumn(
                listOf(
                    ContentLink.native(
                        stringResource(Res.string.preferences_routing_title),
                        ContentRepository.NATIVE_PREFERENCES_ROUTING_ID
                    ),
                    ContentLink.native(
                        stringResource(Res.string.preferences_units_title),
                        ContentRepository.NATIVE_PREFERENCES_UNITS_ID
                    ),
                    ContentLink.Custom(
                        stringResource(Res.string.preferences_location_title),
                        DisclosureType.EXTERNAL,
                        0,
                    ) {
                        openLocationSettings(context)
                    }
                )
            )

            Spacer(Modifier.height(1.rdp))

            ContentLinkColumn(
                listOf(
                    ContentLink.Custom(
                        stringResource(Res.string.preferences_setting_clear_recent_history),
                        DisclosureType.NONE,
                        0,
                    ) {
                        viewModel.clearVisitHistory()
                    },
                    ContentLink.Custom(
                        stringResource(Res.string.preferences_setting_clear_cache),
                        DisclosureType.NONE,
                        0,
                    ) {
                        viewModel.clearCache()
                    }
                )
            )
        }
    }

}

expect fun openLocationSettings(platformContext: PlatformContext)