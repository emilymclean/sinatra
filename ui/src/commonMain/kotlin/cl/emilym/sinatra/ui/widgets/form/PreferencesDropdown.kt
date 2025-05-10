package cl.emilym.sinatra.ui.widgets.form

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cl.emilym.sinatra.data.repository.StatefulPreferencesUnit
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

@Composable
fun <T> PreferencesDropdown(
    unit: StatefulPreferencesUnit<T>,
    options: List<DropdownOption<T>>,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val value by unit.flow.collectAsStateWithLifecycle()

    SinatraDropdown(
        value,
        options,
        { scope.launch { unit.save(it) } },
        modifier = Modifier.then(modifier)
    )
}