package cl.emilym.sinatra.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import cl.emilym.sinatra.asRadians
import cl.emilym.sinatra.data.models.ColorPair
import cl.emilym.sinatra.data.models.CoordinateSpan
import cl.emilym.sinatra.data.models.IRouteTripStop
import cl.emilym.sinatra.data.models.Latitude
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.MapRegion
import cl.emilym.sinatra.data.models.OnColor
import cl.emilym.sinatra.data.models.OnColor.DARK
import cl.emilym.sinatra.data.models.OnColor.LIGHT
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.RouteTripStop
import cl.emilym.sinatra.data.models.TimetableStationTime
import cl.emilym.sinatra.data.models.forDay
import cl.emilym.sinatra.degrees
import cl.emilym.sinatra.ui.widgets.toTodayInstant
import kotlinx.datetime.Instant
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.pow

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

fun <T: IRouteTripStop> List<T>.current(
    now: Instant,
    startOfDay: Instant
): List<T> {
    return filter { it.departureTime != null }.filter { it.departureTime!!.forDay(startOfDay) > now }
}

fun <T: IRouteTripStop> List<T>.past(
    now: Instant,
    startOfDay: Instant
): List<T> {
    return filter { it.departureTime != null }.filter { it.departureTime!!.forDay(startOfDay) < now }
}

@Composable
fun List<TimetableStationTime>.asInstants(): List<Instant> {
    return flatMap { it.times.map { it.time.toTodayInstant() } }
}

fun Float.toCoordinateSpan(
    viewportSize: Size
): CoordinateSpan {
    val span = 360 / 2.0.pow(this.toDouble())
    return CoordinateSpan(
        deltaLatitude = ((viewportSize.width) / 256.0) * span,
        deltaLongitude = ((viewportSize.width) / 256.0) * span
    )
}

fun CoordinateSpan.adjustForLatitude(latitude: Latitude): CoordinateSpan {
    return CoordinateSpan(
        deltaLatitude,
        deltaLongitude * cos(latitude.degrees.asRadians)
    )
}

fun CoordinateSpan.toZoom(): Float {
    return listOf(deltaLatitude, deltaLongitude).map {
        ln(360.0 / it) / ln(2.0)
    }.max().toFloat()
}

fun MapLocation.addCoordinateSpan(span: CoordinateSpan): MapRegion {
    val adjusted = span.adjustForLatitude(lat)
    return MapRegion(
        MapLocation(
            lat + adjusted.deltaLatitude,
            ((lng + adjusted.deltaLongitude + 180) % 360) - 180,
        ),
        MapLocation(
            lat - adjusted.deltaLatitude,
            ((lng - adjusted.deltaLongitude + 180) % 360) - 180
        )
    )
}