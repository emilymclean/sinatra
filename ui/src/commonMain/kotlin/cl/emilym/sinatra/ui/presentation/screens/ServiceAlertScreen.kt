package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.requeststate.RequestStateWidget
import cl.emilym.compose.requeststate.handle
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.ServiceAlert
import cl.emilym.sinatra.data.models.ServiceAlertRegion
import cl.emilym.sinatra.data.repository.ServiceAlertRepository
import cl.emilym.sinatra.ui.asDurationBeforeNow
import cl.emilym.sinatra.ui.widgets.ExternalLinkIcon
import cl.emilym.sinatra.ui.widgets.ListHint
import cl.emilym.sinatra.ui.widgets.SinatraScreenModel
import cl.emilym.sinatra.ui.widgets.WarningIcon
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import cl.emilym.sinatra.ui.widgets.createRequestStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.core.annotation.Factory
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.service_alert_no_alerts
import sinatra.ui.generated.resources.service_alert_region_and_date
import sinatra.ui.generated.resources.service_alert_region_belconnen
import sinatra.ui.generated.resources.service_alert_region_central_canberra
import sinatra.ui.generated.resources.service_alert_region_gungahlin
import sinatra.ui.generated.resources.service_alert_region_other
import sinatra.ui.generated.resources.service_alert_region_tuggeranong
import sinatra.ui.generated.resources.service_alert_region_woden
import sinatra.ui.generated.resources.service_alert_title


val ServiceAlertRegion.text
    @Composable
    get() = when (this) {
        ServiceAlertRegion.BELCONNEN -> stringResource(Res.string.service_alert_region_belconnen)
        ServiceAlertRegion.CENTRAL_CANBERRA -> stringResource(Res.string.service_alert_region_central_canberra)
        ServiceAlertRegion.GUNGAHLIN -> stringResource(Res.string.service_alert_region_gungahlin)
        ServiceAlertRegion.TUGGERANONG -> stringResource(Res.string.service_alert_region_tuggeranong)
        ServiceAlertRegion.WODEN_WESTON_CREEK_MOLONGLO -> stringResource(Res.string.service_alert_region_woden)
        ServiceAlertRegion.OTHER -> stringResource(Res.string.service_alert_region_other)
    }

@Factory
class ServiceAlertViewModel(
    private val serviceAlertRepository: ServiceAlertRepository
): SinatraScreenModel {

    val serviceAlerts = createRequestStateFlow<List<ServiceAlert>>()

    init {
        retryServiceAlerts()
    }

    fun retryServiceAlerts() {
        screenModelScope.launch {
            serviceAlerts.handle {
                serviceAlertRepository.alerts().item
            }
        }
    }

}

class ServiceAlertScreen: Screen {
    override val key: ScreenKey = "service-alert"

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<ServiceAlertViewModel>()
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(Res.string.service_alert_title)) }
                )
            }
        ) { internalPadding ->
            val alerts by viewModel.serviceAlerts.collectAsStateWithLifecycle()
            val uriHandler = LocalUriHandler.current
            Box(
                Modifier.fillMaxSize().padding(internalPadding),
                contentAlignment = Alignment.Center
            ) {
                RequestStateWidget(
                    alerts,
                    retry = { viewModel.retryServiceAlerts() }
                ) { alerts ->
                    if (alerts.isNotEmpty()) {
                        LazyColumn(Modifier.fillMaxSize()) {
                            items(alerts) {
                                Card(
                                    Modifier
                                        .fillMaxWidth()
                                        .then(
                                            it.url?.let {
                                                Modifier.clickable { uriHandler.openUri(it) }
                                            } ?: Modifier
                                        )
                                        .padding(horizontal = 1.rdp),
                                ) {
                                    Row(
                                        Modifier.padding(1.rdp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(1.rdp)
                                    ) {
                                        Column(Modifier.weight(1f)) {
                                            Text(
                                                it.title,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            if (it.regions.isNotEmpty() || it.date != null) {
                                                Text(
                                                    when {
                                                        it.regions.isNotEmpty() && it.date != null ->
                                                            stringResource(
                                                                Res.string.service_alert_region_and_date,
                                                                it.regions.first().text,
                                                                it.date!!.asDurationBeforeNow()
                                                            )
                                                        it.date != null -> it.date!!.asDurationBeforeNow()
                                                        else -> it.regions.first().text
                                                    },
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                        if (it.url != null) {
                                            Box(Modifier.clearAndSetSemantics {  }) {
                                                ExternalLinkIcon(
                                                    tint = MaterialTheme.colorScheme.secondary
                                                )
                                            }
                                        }
                                    }
                                }
                                Spacer(Modifier.height(0.5.rdp))
                            }
                        }
                    } else {
                        ListHint(
                            stringResource(Res.string.service_alert_no_alerts)
                        ) {
                            WarningIcon(tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}