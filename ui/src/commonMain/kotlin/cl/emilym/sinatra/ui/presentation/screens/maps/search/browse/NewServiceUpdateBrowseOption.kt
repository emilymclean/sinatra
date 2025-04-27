package cl.emilym.sinatra.ui.presentation.screens.maps.search.browse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.ui.presentation.screens.ServiceAlertScreen
import cl.emilym.sinatra.ui.widgets.ListCard
import cl.emilym.sinatra.ui.widgets.ServiceAlertCard
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.browse_option_see_all_service_alerts

@Composable
fun NewServiceUpdateBrowseOption(option: BrowsePrompt.NewServiceUpdate) {
    val navigator = LocalNavigator.currentOrThrow
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 1.rdp)
            .clickable { navigator.push(ServiceAlertScreen()) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
    ) {
        ServiceAlertCard(
            option.serviceAlert,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.onSurface,
            )
        )
        ListCard(
            icon = null,
            onClick = { navigator.push(ServiceAlertScreen()) }
        ) {
            Text(stringResource(Res.string.browse_option_see_all_service_alerts))
        }
    }
}