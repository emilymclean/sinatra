package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cl.emilym.compose.units.rdp

@Composable
fun Subheading(
    text: String,
    after: @Composable () -> Unit = {}
) {
    Row(
        Modifier.padding(horizontal = 1.rdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(1.rdp)
    ) {
        Text(
            text,
            style = MaterialTheme.typography.titleMedium
        )
        after()
    }
}