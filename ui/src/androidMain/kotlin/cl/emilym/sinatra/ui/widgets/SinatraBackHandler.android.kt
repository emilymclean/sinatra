package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import androidx.activity.compose.BackHandler

@Composable
actual fun SinatraBackHandler(enabled: Boolean, onBack: () -> Unit) = BackHandler(enabled, onBack)