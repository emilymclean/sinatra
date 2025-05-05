package cl.emilym.sinatra.ui.presentation.screens.preferences

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.ScreenKey
import cl.emilym.sinatra.data.models.ContentLink
import cl.emilym.sinatra.data.repository.ContentRepository
import cl.emilym.sinatra.data.repository.PlatformContext
import cl.emilym.sinatra.ui.widgets.platformContext
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.preferences_location_title
import sinatra.ui.generated.resources.preferences_root_title
import sinatra.ui.generated.resources.preferences_routing_title

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
        return listOf(
            ContentLink.native(
                stringResource(Res.string.preferences_routing_title),
                ContentRepository.NATIVE_PREFERENCES_ROUTING_ID
            ),
            ContentLink.Custom(
                stringResource(Res.string.preferences_location_title),
                true,
                0,
            ) {
                openLocationSettings(context)
            }
        )
    }

}

expect fun openLocationSettings(platformContext: PlatformContext)