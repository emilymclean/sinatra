package cl.emilym.sinatra

import cl.emilym.sinatra.data.models.BCP47LanguageCode
import cl.emilym.sinatra.data.models.Degree
import cl.emilym.sinatra.data.models.LocalizableString
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.MapRegion
import cl.emilym.sinatra.data.models.Radian
import com.google.transit.realtime.TranslatedString
import io.github.aakira.napier.Napier

fun Napier.e(throwable: Throwable) {
    e(throwable.message ?: "Exception", throwable)
}

val Degree.asRadians: Radian
    get() = this * 0.01745f

val Radian.asDegrees: Degree
    get() = this * 57.2958f

val Number.radians: Radian get() = toDouble()
val Number.degrees: Degree get() = toDouble()

fun List<MapLocation>.bounds(): MapRegion {
    val lats = map { it.lat }
    val lngs = map { it.lng }

    return MapRegion(
        MapLocation(lats.max(), lngs.min()),
        MapLocation(lats.min(), lngs.max())
    )
}

fun <T> T?.nullIf(condition: (T) -> Boolean): T? {
    return when {
        this == null -> null
        condition(this) -> null
        else -> this
    }
}

fun <T> List<T>?.nullIfEmpty(): List<T>? {
    return when {
        this.isNullOrEmpty() -> null
        else -> this
    }
}

public inline fun <T> Iterable<T>.sumOfIndexed(selector: (Int,T) -> Int): Int {
    var sum: Int = 0.toInt()
    for (element in this.withIndex()) {
        sum += selector(element.index, element.value)
    }
    return sum
}