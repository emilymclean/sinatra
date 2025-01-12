package cl.emilym.sinatra.ui.localization

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.intl.Locale

fun Float.format(decimalPlaces: Int, separator: Char? = null): String =
    this.toDouble().format(decimalPlaces, separator)

expect fun Double.format(decimalPlaces: Int, separator: Char? = null): String

expect fun decimalSeparator(): Char