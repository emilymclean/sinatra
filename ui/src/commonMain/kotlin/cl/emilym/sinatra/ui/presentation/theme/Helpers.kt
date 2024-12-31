package cl.emilym.sinatra.ui.presentation.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

data class ColorSchemePair(
    val light: ColorScheme,
    val dark: ColorScheme?
)

data class ColorSchemeContrast(
    val default: ColorSchemePair,
    val medium: ColorSchemePair?,
    val high: ColorSchemePair?
)

@Composable
fun ColorSchemeContrast.pickColorSchemeOption(
    darkTheme: Boolean,
    contrastSetting: ContrastSetting
): ColorScheme {
    val pair = when (contrastSetting) {
        ContrastSetting.DEFAULT -> default
        ContrastSetting.MEDIUM -> medium ?: default
        ContrastSetting.HIGH -> high ?: medium ?: default
    }
    return when (darkTheme) {
        false -> pair.light
        true -> pair.dark ?: pair.light
    }
}