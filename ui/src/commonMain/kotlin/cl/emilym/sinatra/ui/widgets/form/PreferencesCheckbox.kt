package cl.emilym.sinatra.ui.widgets.form

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cl.emilym.sinatra.data.repository.StatefulPreferencesUnit
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

@Composable
fun PreferencesCheckbox(
    unit: StatefulPreferencesUnit<Boolean>,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    SinatraCheckbox(
        unit.flow.collectAsStateWithLifecycle().value,
        { scope.launch { unit.save(it) } },
        modifier
    )
}