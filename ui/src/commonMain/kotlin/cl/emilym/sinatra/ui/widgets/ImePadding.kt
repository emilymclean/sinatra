package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import cl.emilym.compose.units.px
import cl.emilym.compose.units.rdp

@Composable
fun imePadding(): Dp {
    val density = LocalDensity.current
    val ime = WindowInsets.ime.getBottom(density).px - WindowInsets.systemBars.getBottom(density).px
    val bottomBar = screenHeight() +
            WindowInsets.systemBars.getBottom(density).px +
            WindowInsets.systemBars.getTop(density).px -
            viewportHeight()
    return (ime - bottomBar + 1.rdp).coerceAtLeast(0.dp)
}