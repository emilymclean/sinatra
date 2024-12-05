package cl.emilym.betterbuscanberra.misc

import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalStdlibApi::class)
fun String.toColor(): Color {
    val h = trimStart { it == '#' }.hexToULong()
    return Color(h)
}