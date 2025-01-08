package cl.emilym.sinatra.room

import cl.emilym.sinatra.data.models.AbstractTime
import cl.emilym.sinatra.data.models.Time
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.milliseconds

internal val Long.time: Time get() = Time.create(this.milliseconds)

internal fun Time.toLong(): Long {
    return (this as AbstractTime).durationThroughDay.inWholeMilliseconds
}

internal fun Time.referenced(startOfDay: Instant?): Time {
    return when {
        startOfDay == null -> this
        else -> addReference(startOfDay)
    }
}