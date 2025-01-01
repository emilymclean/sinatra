package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.content.MediaType.Companion.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.Alert
import cl.emilym.sinatra.data.models.AlertSeverity

@Composable
fun AlertWidget(
    alert: Alert,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        Row(
            Modifier.clip(
                MaterialTheme.shapes.extraLarge
            ).background(
                when (alert.severity) {
                    AlertSeverity.SEVERE -> MaterialTheme.colorScheme.errorContainer
                    AlertSeverity.WARNING -> MaterialTheme.colorScheme.secondaryContainer
                    AlertSeverity.INFO -> MaterialTheme.colorScheme.primaryContainer
                }
            ).padding(1.rdp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(0.5.rdp)
        ) {
            CompositionLocalProvider(
                LocalContentColor provides when (alert.severity) {
                    AlertSeverity.SEVERE -> MaterialTheme.colorScheme.onErrorContainer
                    AlertSeverity.WARNING -> MaterialTheme.colorScheme.onSecondaryContainer
                    AlertSeverity.INFO -> MaterialTheme.colorScheme.onPrimaryContainer
                }
            ) {
                when (alert.severity) {
                    AlertSeverity.SEVERE -> SevereWarningIcon()
                    AlertSeverity.WARNING -> WarningIcon()
                    AlertSeverity.INFO -> InfoIcon()
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(0.25.rdp)
                ) {
                    Text(
                        alert.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    alert.message?.let {
                        Text(it)
                    }
                }
            }
        }
    }
}

@Composable
fun AlertScaffold(
    alerts: List<Alert>
) {
    Box(
        Modifier
            .fillMaxWidth()
            .then(if(alerts.isNotEmpty()) Modifier.padding(1.rdp) else Modifier)
    ) {
        alerts.maxByOrNull { it.severity.ordinal }?.let {
            AlertWidget(it)
        }
    }
}