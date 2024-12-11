package cl.emilym.sinatra

import cl.emilym.sinatra.data.models.MapRegion
import cl.emilym.sinatra.data.models.MapLocation
import io.github.aakira.napier.Napier

fun Napier.e(throwable: Throwable) {
    e(throwable.message ?: "Exception", throwable)
}

val Float.deg
    get() = this * 0.01745f

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