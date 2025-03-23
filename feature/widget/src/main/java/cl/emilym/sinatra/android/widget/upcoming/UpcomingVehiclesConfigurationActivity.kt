package cl.emilym.sinatra.android.widget.upcoming

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cl.emilym.sinatra.android.base.ComposeActivity

class UpcomingVehiclesConfigurationActivity: ComposeActivity() {

    @Composable
    override fun Content() {
        Scaffold {
            Box(Modifier.padding(it)) {
                Text("Test")
            }
        }
    }
}