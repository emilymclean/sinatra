package cl.emilym.sinatra.ui.widgets

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun is24HourTimeFormat(): Boolean {
    return DateFormat.is24HourFormat(LocalContext.current)
}