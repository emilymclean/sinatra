package cl.emilym.sinatra.ui.localization

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

actual fun Double.format(decimalPlaces: Int, separator: Char?): String {
    val separator = separator ?: decimalSeparator()
    return DecimalFormat("#${separator}${"#".repeat(decimalPlaces)}").format(this)
}

actual fun decimalSeparator(): Char {
    return DecimalFormatSymbols.getInstance().decimalSeparator
}