package cl.emilym.sinatra.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.LayoutDirection
import cl.emilym.compose.units.px
import cl.emilym.sinatra.data.models.ColorPair
import cl.emilym.sinatra.data.models.IRouteTripStop
import cl.emilym.sinatra.data.models.Kilometer
import cl.emilym.sinatra.data.models.LocalizableString
import cl.emilym.sinatra.data.models.OnColor
import cl.emilym.sinatra.data.models.OnColor.DARK
import cl.emilym.sinatra.data.models.OnColor.LIGHT
import cl.emilym.sinatra.data.models.Route
import cl.emilym.sinatra.data.models.TimetableStationTime
import cl.emilym.sinatra.ui.localization.toTodayInstant
import kotlinx.datetime.Instant
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.pluralStringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.distance_kilometer
import sinatra.ui.generated.resources.distance_meter
import sinatra.ui.generated.resources.time_hour
import sinatra.ui.generated.resources.time_minute
import sinatra.ui.generated.resources.time_second
import sinatra.ui.generated.resources.time_minute_short
import sinatra.ui.generated.resources.time_second_short
import kotlin.math.roundToInt
import kotlin.time.Duration

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
    return filter { it.departureTime != null }.filter { (it.stationTime?.departure?.time ?: it.departureTime!!).forDay(startOfDay) > now }
}

fun <T: IRouteTripStop> List<T>.past(
    now: Instant,
    startOfDay: Instant
): List<T> {
    return filter { it.departureTime != null }.filter { (it.stationTime?.departure?.time ?: it.departureTime)!!.forDay(startOfDay) <= now }
}

@Composable
fun List<TimetableStationTime>.asInstants(): List<Instant> {
    return flatMap { it.times.map { it.time.toTodayInstant() } }
}

internal expect val Res.string.open_maps: StringResource

val Kilometer.text
    @Composable
    get() = when {
        this < 1 -> {
            val meters = (this * 1000).roundToInt()
            pluralStringResource(Res.plurals.distance_meter, meters, meters)
        }
        else -> {
            val kilometers = roundToInt()
            pluralStringResource(Res.plurals.distance_kilometer, kilometers, kilometers)
        }
    }

val Duration.text
    @Composable
    get() = text(false)

@Composable
fun Duration.text(short: Boolean) = when {
    inWholeSeconds < 60 -> pluralStringResource(if (short) Res.plurals.time_second_short else Res.plurals.time_second, inWholeSeconds.toInt(), inWholeSeconds)
    inWholeMinutes < 60 -> pluralStringResource(if (short) Res.plurals.time_minute_short else Res.plurals.time_minute, inWholeMinutes.toInt(), inWholeMinutes)
    else -> pluralStringResource(Res.plurals.time_hour, inWholeHours.toInt(), inWholeHours)
}

val LocalizableString.text: String
    @Composable
    get() = get(Locale.current.toLanguageTag())

@Composable
fun List<WindowInsets>.asPaddingValues(): PaddingValues {
    val density = LocalDensity.current
    return PaddingValues(
        top = sumOf { it.getTop(density) }.px,
        bottom = sumOf { it.getBottom(density) }.px,
        start = sumOf { it.getLeft(density, LayoutDirection.Ltr) }.px,
        end = sumOf { it.getRight(density, LayoutDirection.Ltr) }.px,
    )
}

@Composable
operator fun PaddingValues.plus(other: PaddingValues): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    return PaddingValues(
        start = this.calculateStartPadding(layoutDirection) + other.calculateStartPadding(layoutDirection),
        top = this.calculateTopPadding() + other.calculateTopPadding(),
        end = this.calculateEndPadding(layoutDirection) + other.calculateEndPadding(layoutDirection),
        bottom = this.calculateBottomPadding() + other.calculateBottomPadding(),
    )
}