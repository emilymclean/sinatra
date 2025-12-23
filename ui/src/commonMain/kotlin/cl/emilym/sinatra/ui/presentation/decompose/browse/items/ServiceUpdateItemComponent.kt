package cl.emilym.sinatra.ui.presentation.decompose.browse.items

import cl.emilym.compose.requeststate.flatRequestStateFlow
import cl.emilym.compose.requeststate.unwrap
import cl.emilym.sinatra.data.models.ServiceAlert
import cl.emilym.sinatra.data.repository.ServiceAlertRepository
import cl.emilym.sinatra.domain.prompt.NewServiceUpdateUseCase
import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponent
import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponentContext
import cl.emilym.sinatra.ui.presentation.decompose.main.NavigationInstruction
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

interface ServiceUpdateItemComponent: SinatraComponent {

    val alert: StateFlow<ServiceAlert?>

    fun onAlertClick()
    fun onViewAllServicesClick()

}

class DefaultServiceUpdateItemComponent(
    private val onNavigate: (NavigationInstruction) -> Unit,
    sinatraComponentContext: SinatraComponentContext
): ServiceUpdateItemComponent, SinatraComponentContext by sinatraComponentContext {

    private val serviceAlertRepository: ServiceAlertRepository = koin.get()
    private val newServiceUpdateUseCase: NewServiceUpdateUseCase = koin.get()

    override val alert: StateFlow<ServiceAlert?> = flatRequestStateFlow {
        newServiceUpdateUseCase()
    }.mapLatest {
        Napier.d("TTTT $it")
        it.unwrap(emptyList()).firstOrNull()
    }.state(null)

    override fun onAlertClick() {
        componentScope.launch {
            serviceAlertRepository.markViewed(
                alert.value?.id ?: return@launch
            )
        }
    }

    override fun onViewAllServicesClick() {
        onNavigate(NavigationInstruction.ServiceAlerts)
    }
}