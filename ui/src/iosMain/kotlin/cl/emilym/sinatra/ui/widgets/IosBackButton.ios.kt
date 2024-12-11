package cl.emilym.sinatra.ui.widgets

import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable

@Composable
actual fun IosBackButton(onBack: () -> Unit) {
    SinatraIconButton(
        onClick = { onBack() }
    ) {
        BackIcon()
    }
}