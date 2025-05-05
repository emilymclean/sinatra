package cl.emilym.sinatra.ui.widgets.form

import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SinatraCheckbox(
    checked: Boolean,
    onCheckedChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Checkbox(
        checked,
        onCheckedChanged,
        modifier,
    )
}

