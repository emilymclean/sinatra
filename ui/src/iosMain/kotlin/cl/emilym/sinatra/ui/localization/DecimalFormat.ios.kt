package cl.emilym.sinatra.ui.localization

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter

actual fun Double.format(decimalPlaces: Int, separator: Char?): String {
    val separator = separator ?: decimalSeparator()
    val formatter = NSNumberFormatter()
    formatter.maximumFractionDigits = decimalPlaces.toULong()
    formatter.decimalSeparator = "$separator"
    return formatter.stringFromNumber(NSNumber(double = this)) ?: "$this"
}

actual fun decimalSeparator(): Char {
    val formatter = NSNumberFormatter()
    return formatter.decimalSeparator[0]
}