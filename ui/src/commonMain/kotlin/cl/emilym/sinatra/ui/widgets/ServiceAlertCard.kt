package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.ServiceAlert
import cl.emilym.sinatra.ui.asDurationFromNow
import cl.emilym.sinatra.ui.presentation.screens.text
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.service_alert_region_and_date

@Composable
fun ServiceAlertCard(
    alert: ServiceAlert,
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface
    ),
    onClick: (() -> Unit)? = null
) {
    val uriHandler = LocalUriHandler.current
    Card(
        Modifier
            .then(
                alert.url?.let {
                    Modifier.clickable {
                        onClick?.invoke()
                        uriHandler.openUri(it)
                    }
                } ?: Modifier
            )
            .then(modifier),
        colors = colors
    ) {
        Row(
            Modifier.padding(1.rdp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(1.rdp)
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    alert.title,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                if (alert.regions.isNotEmpty() || alert.date != null) {
                    Text(
                        when {
                            alert.regions.isNotEmpty() && alert.date != null ->
                                stringResource(
                                    Res.string.service_alert_region_and_date,
                                    alert.regions.first().text,
                                    alert.date!!.asDurationFromNow()
                                )
                            alert.date != null -> alert.date!!.asDurationFromNow()
                            else -> alert.regions.first().text
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            if (alert.url != null) {
                Box(Modifier.clearAndSetSemantics {  }) {
                    ExternalLinkIcon(
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}
