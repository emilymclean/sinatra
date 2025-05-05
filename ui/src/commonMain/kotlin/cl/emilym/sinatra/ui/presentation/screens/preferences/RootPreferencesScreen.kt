package cl.emilym.sinatra.ui.presentation.screens.preferences

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.ScreenKey
import cl.emilym.sinatra.data.repository.state
import cl.emilym.sinatra.lib.FloatRange
import cl.emilym.sinatra.ui.text
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import cl.emilym.sinatra.ui.widgets.form.HorizontalLockup
import cl.emilym.sinatra.ui.widgets.form.PreferencesCheckbox
import cl.emilym.sinatra.ui.widgets.form.PreferencesFloatSlider
import cl.emilym.sinatra.ui.widgets.form.VerticalLockup
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.preferences_root_title
import sinatra.ui.generated.resources.preferences_setting_bikes
import sinatra.ui.generated.resources.preferences_setting_bikes_subtitle
import sinatra.ui.generated.resources.preferences_setting_max_walking
import sinatra.ui.generated.resources.preferences_setting_wheelchair
import sinatra.ui.generated.resources.preferences_setting_wheelchair_subtitle
import kotlin.time.Duration.Companion.minutes

class RootPreferencesScreen: PreferencesScreen() {
    override val key: ScreenKey = "preferences-root"

    override val title: String
        @Composable
        get() = stringResource(Res.string.preferences_root_title)

    @Composable
    override fun ColumnScope.Preferences(preferencesCollection: PreferencesCollection) {
        HorizontalLockup(
            stringResource(Res.string.preferences_setting_wheelchair),
            stringResource(Res.string.preferences_setting_wheelchair_subtitle),
            Modifier.fillMaxWidth()
        ) {
            PreferencesCheckbox(preferencesCollection.requiresWheelchair)
        }

        HorizontalLockup(
            stringResource(Res.string.preferences_setting_bikes),
            stringResource(Res.string.preferences_setting_bikes_subtitle),
            Modifier.fillMaxWidth()
        ) {
            PreferencesCheckbox(preferencesCollection.requiresBikes)
        }

        VerticalLockup(
            stringResource(Res.string.preferences_setting_max_walking),
            null,
            Modifier.fillMaxWidth()
        ) {
            PreferencesFloatSlider(
                preferencesCollection.maximumWalkingTime,
                FloatRange(10f, 60f),
                Modifier.fillMaxWidth(1f)
            ) {
                Text(
                    it.toDouble().minutes.text
                )
            }
        }
    }
}