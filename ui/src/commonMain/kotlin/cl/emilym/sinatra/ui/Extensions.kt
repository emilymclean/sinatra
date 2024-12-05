package cl.emilym.sinatra.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import cl.emilym.betterbuscanberra.data.models.ColorPair
import cl.emilym.betterbuscanberra.data.models.OnColor
import cl.emilym.betterbuscanberra.data.models.OnColor.DARK
import cl.emilym.betterbuscanberra.data.models.OnColor.LIGHT

@OptIn(ExperimentalStdlibApi::class)
fun String.toColor(): Color {
    val h = trimStart { it == '#' }.hexToULong()
    return Color(h)
}

@Composable
fun OnColor.color() = when (this) {
    DARK -> Color.Black
    LIGHT -> Color.White
}

@Composable
fun ColorPair.color() = color.toColor()
@Composable
fun ColorPair.onColor() = onColor.color()