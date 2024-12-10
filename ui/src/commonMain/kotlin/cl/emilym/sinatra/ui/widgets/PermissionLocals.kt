package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.compositionLocalOf

data class PermissionState(
    val hasLocationPermission: Boolean = false
)

val LocalPermissionState = compositionLocalOf { PermissionState() }