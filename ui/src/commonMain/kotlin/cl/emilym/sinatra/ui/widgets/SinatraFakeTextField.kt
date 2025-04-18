package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cl.emilym.sinatra.nullIfBlank

@Composable
fun SinatraFakeTextField(
    value: String?,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    onClick: (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Row(
        Modifier
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            )
            .padding(16.dp)
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        leadingIcon?.let { it() }
        value.nullIfBlank()?.let {
            Text(it)
        } ?: placeholder?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        trailingIcon?.let { it() }
    }
}