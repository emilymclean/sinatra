package cl.emilym.sinatra.ui.localization

fun Float.format(decimalPlaces: Int, separator: Char? = null): String =
    this.toDouble().format(decimalPlaces, separator)

expect fun Double.format(decimalPlaces: Int, separator: Char? = null): String

expect fun decimalSeparator(): Char