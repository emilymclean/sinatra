package cl.emilym.sinatra.ui.widgets

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun SinatraBackHandler(enabled: Boolean, onBack: () -> Unit) = BackHandler(enabled, onBack)