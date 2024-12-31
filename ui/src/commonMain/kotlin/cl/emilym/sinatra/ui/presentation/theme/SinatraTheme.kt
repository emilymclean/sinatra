package cl.emilym.sinatra.ui.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver

val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

val Container
    @Composable
    get() = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f).compositeOver(MaterialTheme.colorScheme.background)

@Composable
expect fun dynamicColorScheme(darkTheme: Boolean, fallback: ColorScheme): ColorScheme

@Composable
fun SinatraTheme(
    colorSchemeOption: ColorSchemeOption = ColorSchemeOption.Default,
    darkTheme: Boolean = isSystemInDarkTheme(),
    contrastSetting: ContrastSetting = ContrastSetting.DEFAULT,
    content: @Composable () -> Unit
) {
    val color = colorSchemeOption.provider(darkTheme, contrastSetting)
    MaterialTheme(
        colorScheme = when (colorSchemeOption.overrideDynamic) {
            false -> dynamicColorScheme(darkTheme, color)
            true -> color
        },
        typography = Typography,
        content = content
    )
}

@Composable
fun defaultLineColor(): Color = MaterialTheme.colorScheme.onSurface