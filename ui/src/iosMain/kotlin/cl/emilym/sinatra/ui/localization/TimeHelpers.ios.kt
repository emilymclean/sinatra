package cl.emilym.sinatra.ui.localization

import androidx.compose.runtime.Composable
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterNoStyle
import platform.Foundation.NSDateFormatterShortStyle

@Composable
actual fun is24HourTimeFormat(): Boolean {
//    val formatter = NSDateFormatter()
//    formatter.setDateStyle(NSDateFormatterNoStyle)
//    formatter.setTimeStyle(NSDateFormatterShortStyle)
//    val dateString = formatter.stringFromDate(NSDate())
//    return !dateString.contains("AM") && !dateString.contains("PM")
    return false
}