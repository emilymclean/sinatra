package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import cl.emilym.compose.units.rdp

@Composable
fun AccessibilityIconLockup(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(0.5.rdp),
        modifier = Modifier.fillMaxWidth()
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.primary) {
            icon()
        }
        text()
    }
}