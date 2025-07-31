package cl.emilym.sinatra.ui.presentation.screens.preferences

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.ScreenKey
import cl.emilym.sinatra.data.models.Time24HSetting
import cl.emilym.sinatra.data.repository.Preference
import cl.emilym.sinatra.data.repository.isIos
import cl.emilym.sinatra.ui.widgets.form.DropdownOption
import cl.emilym.sinatra.ui.widgets.form.HorizontalLockup
import cl.emilym.sinatra.ui.widgets.form.PreferencesDropdown
import cl.emilym.sinatra.ui.widgets.form.SinatraCheckbox
import cl.emilym.sinatra.ui.widgets.form.VerticalLockup
import cl.emilym.sinatra.ui.widgets.rememberPreferenceState
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.preferences_setting_metric
import sinatra.ui.generated.resources.preferences_setting_metric_imperial
import sinatra.ui.generated.resources.preferences_setting_metric_metric
import sinatra.ui.generated.resources.preferences_setting_metric_subtitle
import sinatra.ui.generated.resources.preferences_setting_time
import sinatra.ui.generated.resources.preferences_setting_time_12h
import sinatra.ui.generated.resources.preferences_setting_time_24h
import sinatra.ui.generated.resources.preferences_setting_time_automatic
import sinatra.ui.generated.resources.preferences_setting_time_subtitle
import sinatra.ui.generated.resources.preferences_setting_time_subtitle_alt
import sinatra.ui.generated.resources.preferences_units_title

class UnitsPreferencesScreen: PreferencesScreen() {
    override val key: ScreenKey = "preferences-units"
    override val title: String
        @Composable
        get() = stringResource(Res.string.preferences_units_title)

    @Composable
    override fun ColumnScope.Preferences() {
        VerticalLockup(
            stringResource(Res.string.preferences_setting_metric),
            stringResource(Res.string.preferences_setting_metric_subtitle),
            Modifier.fillMaxWidth()
        ) {
            PreferencesDropdown(
                Preference.MetricUnits,
                listOf(
                    DropdownOption(true, stringResource(Res.string.preferences_setting_metric_metric)),
                    DropdownOption(false, stringResource(Res.string.preferences_setting_metric_imperial)),
                )
            )
        }

        if (isIos) {
            val scope = rememberCoroutineScope()
            HorizontalLockup(
                stringResource(Res.string.preferences_setting_time),
                stringResource(Res.string.preferences_setting_time_subtitle),
                Modifier.fillMaxWidth()
            ) {
                var value by rememberPreferenceState(Preference.Use24HourUnits)
                SinatraCheckbox(
                    when (value) {
                        Time24HSetting.OVERRIDE_24 -> true
                        else -> false
                    },
                    onCheckedChanged = {
                        value = when (it) {
                            true -> Time24HSetting.OVERRIDE_24
                            else -> Time24HSetting.AUTOMATIC
                        }
                    }
                )
            }
        } else {
            VerticalLockup(
                stringResource(Res.string.preferences_setting_time),
                stringResource(Res.string.preferences_setting_time_subtitle_alt),
                Modifier.fillMaxWidth()
            ) {
                PreferencesDropdown(
                    Preference.Use24HourUnits,
                    listOf(
                        DropdownOption(Time24HSetting.AUTOMATIC, stringResource(Res.string.preferences_setting_time_automatic)),
                        DropdownOption(Time24HSetting.OVERRIDE_12, stringResource(Res.string.preferences_setting_time_12h)),
                        DropdownOption(Time24HSetting.OVERRIDE_24, stringResource(Res.string.preferences_setting_time_24h)),
                    )
                )
            }
        }
    }
}