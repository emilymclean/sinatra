package cl.emilym.sinatra.ui.widgets.form

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cl.emilym.sinatra.data.repository.Preference
import cl.emilym.sinatra.ui.widgets.rememberPreferenceState

@Composable
fun <T> PreferencesDropdown(
    preference: Preference<T>,
    options: List<DropdownOption<T>>,
    modifier: Modifier = Modifier
) {
    var value by rememberPreferenceState(preference)
    SinatraDropdown(
        value,
        options,
        { value = it },
        modifier = Modifier.then(modifier)
    )
}