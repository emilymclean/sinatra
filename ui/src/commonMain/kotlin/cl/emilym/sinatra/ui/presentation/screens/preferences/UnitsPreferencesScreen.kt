package cl.emilym.sinatra.ui.presentation.screens.preferences

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.ScreenKey
import cl.emilym.sinatra.ui.widgets.form.HorizontalLockup
import cl.emilym.sinatra.ui.widgets.form.PreferencesCheckbox
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.preferences_setting_metric
import sinatra.ui.generated.resources.preferences_units_title

class UnitsPreferencesScreen: PreferencesScreen() {
    override val key: ScreenKey = "preferences-units"
    override val title: String
        @Composable
        get() = stringResource(Res.string.preferences_units_title)

    @Composable
    override fun ColumnScope.Preferences(preferencesCollection: PreferencesCollection) {
        HorizontalLockup(
            stringResource(Res.string.preferences_setting_metric),
            null,
            Modifier.fillMaxWidth()
        ) {
            PreferencesCheckbox(preferencesCollection.metric)
        }
    }
}