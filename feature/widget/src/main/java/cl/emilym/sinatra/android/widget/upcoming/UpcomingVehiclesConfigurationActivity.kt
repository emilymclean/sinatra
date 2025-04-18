package cl.emilym.sinatra.android.widget.upcoming

import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.emilym.sinatra.android.base.ComposeActivity
import cl.emilym.sinatra.android.widget.R
import cl.emilym.sinatra.android.widget.data.repository.UpcomingVehiclesWidgetRepository
import cl.emilym.sinatra.data.models.Heading
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.nullIf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

sealed interface UpcomingVehiclesConfigurationState {
    data object InvalidAppWidget: UpcomingVehiclesConfigurationState
    data class ConfigurationEntry(
        val validConfiguration: Boolean
    ): UpcomingVehiclesConfigurationState
}

@KoinViewModel
class UpcomingVehiclesConfigurationViewModel(
    private val upcomingVehiclesWidgetRepository: UpcomingVehiclesWidgetRepository
): ViewModel() {

    val stopId = MutableStateFlow<StopId?>(null)
    val routeId = MutableStateFlow<RouteId?>(null)
    val heading = MutableStateFlow<Heading?>(null)

    private val _isValid = MutableStateFlow(true)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state = _isValid.flatMapLatest {
        when (it) {
            false -> flowOf(UpcomingVehiclesConfigurationState.InvalidAppWidget)
            true -> stopId.mapLatest { stopId ->
                UpcomingVehiclesConfigurationState.ConfigurationEntry(
                    !stopId.isNullOrBlank()
                )
            }
        }
    }

    fun init(appWidgetId: Int?) {
        if (appWidgetId == null) {
            _isValid.value = false
            return
        }

        viewModelScope.launch {
            val existingConfig = upcomingVehiclesWidgetRepository.get(appWidgetId) ?: return@launch
            stopId.value = existingConfig.stopId
            routeId.value = existingConfig.routeId
            heading.value = existingConfig.heading
        }
    }

}

class UpcomingVehiclesConfigurationActivity: ComposeActivity() {

    val viewModel by viewModel<UpcomingVehiclesConfigurationViewModel>()

    private val appWidgetId get() = intent?.extras?.getInt(
        AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID
    )?.nullIf { it == 0 }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.init(appWidgetId)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        Scaffold(
           topBar = {
               TopAppBar(
                   title = {
                       Text(stringResource(R.string.upcoming_vehicle_widget_label))
                   }
               )
           }
        ) {
            Box(Modifier.padding(it)) {
                LazyColumn(
                    Modifier.fillMaxSize()
                ) {

                }
            }
        }
    }
}