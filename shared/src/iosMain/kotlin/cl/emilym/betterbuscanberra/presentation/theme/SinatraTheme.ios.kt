package cl.emilym.betterbuscanberra.presentation.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun pickColorScheme(dynamicColor: Boolean, darkTheme: Boolean): ColorScheme {
    return when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
}