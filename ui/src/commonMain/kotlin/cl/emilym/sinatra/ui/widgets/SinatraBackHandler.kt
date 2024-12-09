package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable

@Composable
expect fun SinatraBackHandler(enabled: Boolean, onBack: () -> Unit)