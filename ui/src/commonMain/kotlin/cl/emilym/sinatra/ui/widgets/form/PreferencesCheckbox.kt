package cl.emilym.sinatra.ui.widgets.form

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cl.emilym.sinatra.data.repository.Preference
import cl.emilym.sinatra.ui.widgets.rememberPreferenceState

@Composable
fun PreferencesCheckbox(
    preference: Preference<Boolean>,
    modifier: Modifier = Modifier
) {
    var value by rememberPreferenceState(preference)
    SinatraCheckbox(
        value,
        { value = it },
        modifier
    )
}