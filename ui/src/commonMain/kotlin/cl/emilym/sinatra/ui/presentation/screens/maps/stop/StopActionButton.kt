package cl.emilym.sinatra.ui.presentation.screens.maps.stop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cl.emilym.compose.units.rdp

@Composable
fun StopActionButton(
    icon: (@Composable () -> Unit)?,
    text: String,
    highlighted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = Modifier.then(modifier),
        colors = when (highlighted) {
            true -> ButtonDefaults.buttonColors()
            else -> ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) {
        icon?.let {
            it()
            Box(Modifier.width(0.5.rdp))
        }
        Text(text)
    }
}