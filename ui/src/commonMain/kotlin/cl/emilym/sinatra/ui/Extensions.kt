package cl.emilym.sinatra.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.sinatra.data.models.ColorPair
import cl.emilym.sinatra.data.models.OnColor
import cl.emilym.sinatra.data.models.OnColor.DARK
import cl.emilym.sinatra.data.models.OnColor.LIGHT
import cl.emilym.sinatra.data.models.Route

fun String.toColor(): Color {
    val trimmed = removePrefix("#")
    var colorInt = trimmed.toLong(radix = 16)
    if (trimmed.length == 6) {
        colorInt = 0x00000000ff000000 or colorInt
    }
    return Color(
        alpha = (colorInt.shr(24) and 0xFF).toInt(),
        red = (colorInt.shr(16) and 0xFF).toInt(),
        green = (colorInt.shr(8) and 0xFF).toInt(),
        blue = (colorInt.shr(0) and 0xFF).toInt()
    )
}

@Composable
fun OnColor.color() = when (this) {
    DARK -> Color.Black
    LIGHT -> Color.White
}

@Composable
fun ColorPair.color(): Color {
    return color.toColor()
}
@Composable
fun ColorPair.onColor() = onColor.color()

@Composable
fun Route.color(): Color {
    return when {
        code == "8" && isSystemInDarkTheme() -> MaterialTheme.colorScheme.onSurface
        colors != null -> colors!!.color()
        else -> MaterialTheme.colorScheme.onSurface
    }
}

@Composable
fun Route.onColor(): Color? {
    return when {
        code == "8" && isSystemInDarkTheme() -> MaterialTheme.colorScheme.surface
        colors != null -> colors!!.onColor()
        else -> MaterialTheme.colorScheme.surface
    }
}