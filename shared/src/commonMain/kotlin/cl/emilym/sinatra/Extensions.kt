package cl.emilym.sinatra

import cl.emilym.sinatra.data.models.Bounds
import cl.emilym.sinatra.data.models.Location
import cl.emilym.sinatra.data.models.RouteTripStop
import cl.emilym.sinatra.data.models.Stop
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

fun Napier.e(throwable: Throwable) {
    e(throwable.message ?: "Exception", throwable)
}

val Float.deg
    get() = this * 0.01745f

fun List<Location>.bounds(): Bounds {
    val lats = map { it.lat }
    val lngs = map { it.lng }

    return Bounds(
        Location(lats.max(), lngs.min()),
        Location(lats.min(), lngs.max())
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