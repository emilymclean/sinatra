package cl.emilym.sinatra.android.widget.upcoming

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
import cl.emilym.sinatra.android.base.ComposeActivity
import cl.emilym.sinatra.android.widget.R
import org.koin.android.annotation.KoinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

@KoinViewModel
class UpcomingVehiclesConfigurationViewModel(
    
): ViewModel() {

}

class UpcomingVehiclesConfigurationActivity: ComposeActivity() {
    val viewModel by viewModel<UpcomingVehiclesConfigurationViewModel>()

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