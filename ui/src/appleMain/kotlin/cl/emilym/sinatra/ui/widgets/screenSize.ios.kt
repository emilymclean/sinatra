package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun screenWidth(): Dp = LocalWindowInfo.current.containerSize.width.toDp()

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun screenHeight(): Dp = LocalWindowInfo.current.containerSize.height.toDp()