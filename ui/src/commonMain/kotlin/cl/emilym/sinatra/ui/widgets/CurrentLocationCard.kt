package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cl.emilym.compose.units.rdp
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.current_location

@Composable
fun CurrentLocationCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    showCurrentLocationIcon: Boolean = false,
) {
    ListCard(
        {
            if (showCurrentLocationIcon) {
                RandleScaffold {
                    MyLocationIcon()
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
                stringResource(Res.string.current_location),
                modifier = Modifier.weight(1f, fill = false),
            )
        }
    }
}