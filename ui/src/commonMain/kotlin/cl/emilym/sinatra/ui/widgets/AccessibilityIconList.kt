package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import cl.emilym.compose.units.rdp

@Composable
fun AccessibilityIconLockup(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(0.5.rdp),
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {  }
    ) {
        Box(Modifier.clearAndSetSemantics {  }) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.primary) {
                icon()
            }
        }
        text()
    }
}