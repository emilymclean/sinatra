package cl.emilym.sinatra.ui.widgets.form

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cl.emilym.sinatra.data.repository.Preference
import cl.emilym.sinatra.lib.FloatRange
import cl.emilym.sinatra.ui.widgets.rememberPreferenceState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesFloatSlider(
    preference: Preference<Float>,
    range: FloatRange,
    modifier: Modifier = Modifier,
    valueDisplay: (@Composable (Float) -> Unit)? = null
) {
    var value by rememberPreferenceState(preference)
    FloatSlider(
        value,
        onValueChanged = { value = it },
        range = range,
        modifier = modifier,
        valueDisplay = valueDisplay
    )
}