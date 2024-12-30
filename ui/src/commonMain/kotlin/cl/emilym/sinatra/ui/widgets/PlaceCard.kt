package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.Place

@Composable
fun PlaceCard(
    place: Place,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    showPlaceIcon: Boolean = false,
) {
    ListCard(
        {
            if (showPlaceIcon) {
                RandleScaffold {
                    GenericMarkerIcon()
                }
            }
        },
        modifier,
        onClick,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(0.5.rdp)
        ) {
            Text(
                place.name,
                modifier = Modifier.weight(1f, fill = false),
            )
        }
    }
}