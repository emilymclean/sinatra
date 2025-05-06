package cl.emilym.sinatra.ui.widgets.form

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cl.emilym.sinatra.ui.widgets.DropdownDownIcon
import cl.emilym.sinatra.ui.widgets.DropdownUpIcon

data class DropdownOption<T>(
    val value: T,
    val label: String
)

@Composable
fun <T> SinatraDropdown(
    value: T?,
    options: List<DropdownOption<T>>,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedOption = remember(value, options) { value?.let { options.firstOrNull { it.value == value } } }
    var open by remember { mutableStateOf(false) }

    TextFieldBox(
        Modifier
            .animateContentSize()
            .then(modifier)
    ) {
        Column {
            TextFieldRow(
                Modifier.clickable {
                    open = !open
                },
                trailingIcon = {
                    when (open) {
                        true -> DropdownUpIcon()
                        false -> DropdownDownIcon()
                    }
                }
            ) {
                selectedOption?.let {
                    Text(it.label)
                }
            }

            if (open) {
                for (option in options) {
                    HorizontalDivider()
                    TextFieldRow(
                        Modifier.clickable {
                            onSelect(option.value)
                            open = false
                        }
                    ) {
                        Text(option.label)
                    }
                }
            }
        }
    }
}