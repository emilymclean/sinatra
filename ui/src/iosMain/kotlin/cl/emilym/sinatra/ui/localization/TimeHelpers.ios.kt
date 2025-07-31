package cl.emilym.sinatra.ui.localization

import androidx.compose.runtime.Composable
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

@Composable
actual fun is24HourTimeFormatInternal(): Boolean {
    val locale = NSLocale.currentLocale
    val formatter = NSDateFormatter.dateFormatFromTemplate(
        tmplate = "j",
        options = 0U,
        locale = locale
    )
    return formatter?.contains("a") != true
}