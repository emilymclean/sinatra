package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
actual fun screenWidth(): Dp = LocalConfiguration.current.screenWidthDp.dp

@Composable
actual fun screenHeight(): Dp = LocalConfiguration.current.screenHeightDp.dp