package cl.emilym.sinatra.ui.widgets

import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import cl.emilym.sinatra.data.repository.isIos

val localTextStyleFixIos
    @Composable
    get() = LocalTextStyle.current.fixIos()

fun TextStyle.fixIos(): TextStyle = when {
    isIos -> copy(
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both
        )
    )
    else -> this
}