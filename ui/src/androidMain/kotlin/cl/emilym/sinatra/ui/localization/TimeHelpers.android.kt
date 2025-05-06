package cl.emilym.sinatra.ui.localization

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun is24HourTimeFormatInternal(): Boolean {
    return DateFormat.is24HourFormat(LocalContext.current)
}