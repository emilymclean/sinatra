package cl.emilym.sinatra.ui.presentation.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun dynamicColorScheme(darkTheme: Boolean, fallback: ColorScheme): ColorScheme {
    return fallback
}