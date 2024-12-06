package cl.emilym.sinatra.ui.presentation.screens.maps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.ui.presentation.screens.MapsScreen
import cl.emilym.sinatra.ui.widgets.PillShape

class MapSearchScreen: MapsScreen() {
    override val needsMapHandle: Boolean = true

    @Composable
    override fun MainContent() {
        Box(Modifier.padding(1.rdp)) {
            Row(
                Modifier
                    .shadow(10.dp, shape = PillShape)
                    .clip(PillShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .fillMaxWidth()
                    .padding(0.5.rdp)
            ) {
                Text("Test")
            }
        }
    }
}