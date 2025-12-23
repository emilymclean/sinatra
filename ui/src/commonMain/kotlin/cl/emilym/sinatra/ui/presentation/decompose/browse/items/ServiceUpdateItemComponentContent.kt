package cl.emilym.sinatra.ui.presentation.decompose.browse.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cl.emilym.compose.units.px
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.ui.presentation.screens.ServiceAlertScreen
import cl.emilym.sinatra.ui.widgets.ListCard
import cl.emilym.sinatra.ui.widgets.ServiceAlertCard
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.browse_option_see_all_service_alerts

@Composable
fun ServiceUpdateItemComponentContent(
    component: ServiceUpdateItemComponent
) {
    val alert by component.alert.collectAsStateWithLifecycle()
    Box(Modifier.height(1.px))
    if (alert == null) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 1.rdp)
            .clickable { component.onViewAllServicesClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
    ) {
        ServiceAlertCard(
            alert ?: return@Card,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
            onClick = { component.onAlertClick() }
        )
        ListCard(
            icon = null,
            onClick = { component.onViewAllServicesClick() }
        ) {
            Text(stringResource(Res.string.browse_option_see_all_service_alerts))
        }
    }
}