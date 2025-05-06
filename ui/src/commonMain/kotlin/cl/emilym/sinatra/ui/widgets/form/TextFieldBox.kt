package cl.emilym.sinatra.ui.widgets.form

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun TextFieldBox(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        Modifier
            .background(
                MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = MaterialTheme.shapes.large
            )
            .clip(MaterialTheme.shapes.large)
            .heightIn(min = 56.dp)
            .widthIn(min = 280.dp)
            .then(modifier)
    ) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onSurface
        ) {
            content()
        }
    }
}

@Composable
fun TextFieldRow(
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Row(
        Modifier
            .then(modifier)
            .heightIn(min = 56.dp)
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        leadingIcon?.let {
            Box {
                leadingIcon()
            }
        }
        Box(Modifier.weight(1f)) {
            content()
        }
        trailingIcon?.let {
            Box {
                trailingIcon()
            }
        }
    }
}