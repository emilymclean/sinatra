package cl.emilym.sinatra.ui.widgets

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import cl.emilym.compose.units.px
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.Alert
import cl.emilym.sinatra.data.models.AlertSeverity
import cl.emilym.sinatra.ui.text
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.alert_default_title

val Alert.Realtime.title: String?
    @Composable
    get() {
        val effect = when (effect) {
            is com.google.transit.realtime.Alert.Effect.ACCESSIBILITY_ISSUE -> "accessibility issue"
            is com.google.transit.realtime.Alert.Effect.DETOUR -> "detour"
            is com.google.transit.realtime.Alert.Effect.ADDITIONAL_SERVICE -> "additional service"
            is com.google.transit.realtime.Alert.Effect.MODIFIED_SERVICE -> "modified service"
            is com.google.transit.realtime.Alert.Effect.REDUCED_SERVICE -> "reduced service"
            is com.google.transit.realtime.Alert.Effect.SIGNIFICANT_DELAYS -> "significant delays"
            is com.google.transit.realtime.Alert.Effect.STOP_MOVED -> "stop moved"
            else -> null
        }

        val cause = when (cause) {
            is com.google.transit.realtime.Alert.Cause.STRIKE -> "industrial action"
            is com.google.transit.realtime.Alert.Cause.ACCIDENT -> "accident"
            is com.google.transit.realtime.Alert.Cause.CONSTRUCTION -> "construction"
            is com.google.transit.realtime.Alert.Cause.DEMONSTRATION -> "demonstration"
            is com.google.transit.realtime.Alert.Cause.HOLIDAY -> "holiday"
            is com.google.transit.realtime.Alert.Cause.MAINTENANCE -> "maintenance"
            is com.google.transit.realtime.Alert.Cause.MEDICAL_EMERGENCY -> "medical emergency"
            is com.google.transit.realtime.Alert.Cause.POLICE_ACTIVITY -> "police activity"
            is com.google.transit.realtime.Alert.Cause.TECHNICAL_PROBLEM -> "technical issues"
            is com.google.transit.realtime.Alert.Cause.WEATHER -> "weather"
            else -> null
        }

        return when {
            cause != null && effect != null -> "${effect.capitalize()} due to $cause"
            effect != null -> effect.capitalize()
            cause != null -> cause.capitalize()
            else -> null
        }
    }

val Alert.title: String?
    @Composable
    get() = when(this) {
        is Alert.Content -> title
        is Alert.Realtime -> title
    }

val Alert.message: String?
    @Composable
    get() = when(this) {
        is Alert.Content -> message
        is Alert.Realtime -> headerText?.text
    }

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
                        alert.title ?: stringResource(Res.string.alert_default_title),
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
    alerts: List<Alert>?
) {
    Box(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
            .heightIn(min = 1.px) // Animation doesn't work if starting from 0 height >:P
            .then(if(alerts.isNullOrEmpty()) Modifier else Modifier.padding(1.rdp))
    ) {
        alerts
            ?.filterNot { it.title == null && it.message == null }
            ?.maxByOrNull { it.severity.ordinal }
            ?.let {
                AlertWidget(it)
            }
    }
}