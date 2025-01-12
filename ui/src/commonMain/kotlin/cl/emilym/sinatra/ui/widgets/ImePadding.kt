package cl.emilym.sinatra.ui.widgets

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import cl.emilym.compose.units.px
import cl.emilym.compose.units.rdp
import io.github.aakira.napier.Napier

@Composable
fun imePadding(): Dp {
    val density = LocalDensity.current
    val ime = WindowInsets.ime.getBottom(density).px - WindowInsets.systemBars.getBottom(density).px
    val bottomBar = screenHeight() +
            WindowInsets.systemBars.getBottom(density).px +
            WindowInsets.systemBars.getTop(density).px -
            viewportHeight()
    LaunchedEffect(bottomBar, ime) {
        Napier.d("bottomBar = ${bottomBar}, ime = ${ime}")
    }
    return (ime - bottomBar + 1.rdp).coerceAtLeast(0.dp)
}