package cl.emilym.sinatra.ui.presentation.screens.preferences

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.ScreenKey
import cl.emilym.sinatra.ui.widgets.form.DropdownOption
import cl.emilym.sinatra.ui.widgets.form.PreferencesDropdown
import cl.emilym.sinatra.ui.widgets.form.VerticalLockup
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.preferences_setting_metric
import sinatra.ui.generated.resources.preferences_units_title
import sinatra.ui.generated.resources.preferences_setting_metric_metric
import sinatra.ui.generated.resources.preferences_setting_metric_imperial
import sinatra.ui.generated.resources.preferences_setting_metric_subtitle

class UnitsPreferencesScreen: PreferencesScreen() {
    override val key: ScreenKey = "preferences-units"
    override val title: String
        @Composable
        get() = stringResource(Res.string.preferences_units_title)

    @Composable
    override fun ColumnScope.Preferences(preferencesCollection: PreferencesCollection) {
        VerticalLockup(
            stringResource(Res.string.preferences_setting_metric),
            stringResource(Res.string.preferences_setting_metric_subtitle),
            Modifier.fillMaxWidth()
        ) {
            PreferencesDropdown(
                preferencesCollection.metric,
                listOf(
                    DropdownOption(true, stringResource(Res.string.preferences_setting_metric_metric)),
                    DropdownOption(false, stringResource(Res.string.preferences_setting_metric_imperial)),
                )
            )
        }
    }
}