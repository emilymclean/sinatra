package cl.emilym.sinatra.ui.widgets.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import cl.emilym.compose.units.rdp

val titleTextStyle: TextStyle
    @Composable
    get() = MaterialTheme.typography.bodyLarge
val subtitleTextStyle: TextStyle
    @Composable
    get() = MaterialTheme.typography.bodyMedium

@Composable
fun VerticalLockup(
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        Modifier.then(modifier)
    ) {
        Text(title, style = titleTextStyle)
        subtitle?.let { Text(it, style = subtitleTextStyle) }
        Spacer(Modifier.height(0.5.rdp))
        content()
    }
}

@Composable
fun HorizontalLockup(
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(1.rdp)
    ) {
        Column(
            Modifier.weight(1f)
        ) {
            Text(title, style = titleTextStyle)
            subtitle?.let { Text(it, style = subtitleTextStyle) }
        }
        Column {
            content()
        }
    }
}