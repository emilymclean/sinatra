package cl.emilym.sinatra.ui.presentation.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import cl.emilym.sinatra.ui.presentation.theme.regular.regularColorScheme
import cl.emilym.sinatra.ui.presentation.theme.trans.transColorScheme

enum class ContrastSetting {
    DEFAULT,
    MEDIUM,
    HIGH
}

typealias ColorSchemeProvider = @Composable (
    darkTheme: Boolean,
    contrastSetting: ContrastSetting
) -> ColorScheme

sealed interface ColorSchemeOption {
    val provider: ColorSchemeProvider
    val overrideDynamic: Boolean
        get() = false

    data object Default: ColorSchemeOption {
        override val provider: ColorSchemeProvider = { darkTheme, contrastSetting ->
            regularColorScheme(darkTheme, contrastSetting)
        }
    }

    data object BuiltIn: ColorSchemeOption {
        override val provider: ColorSchemeProvider = { darkTheme, contrastSetting ->
            regularColorScheme(darkTheme, contrastSetting)
        }
        override val overrideDynamic: Boolean = true
    }

    data object Trans: ColorSchemeOption {
        override val provider: ColorSchemeProvider = { darkTheme, contrastSetting ->
            transColorScheme(darkTheme, contrastSetting)
        }
        override val overrideDynamic: Boolean = true
    }
}