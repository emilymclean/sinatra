package cl.emilym.sinatra.data.models

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

typealias ShaDigest = String
typealias ResourceKey = String

typealias StopId = String
typealias RouteId = String
typealias RouteCode = String
typealias ServiceId = String
typealias TripId = String
typealias Latitude = Double
typealias Longitude = Double
typealias Pixel = Int

typealias Radian = Double
typealias Degree = Double

typealias Time = Duration

typealias MarkdownString = String
typealias ContentId = String

fun parseTime(time: String): Time {
    return Duration.parseIsoString(time)
}

val Long.time: Duration get() = this.milliseconds

fun Time.forDay(instant: Instant): Instant {
    return instant + this
}

fun Time.toLong(): Long {
    return inWholeMilliseconds
}

fun Instant.toTodayTime(startOfDay: Instant): Time {
    return this - startOfDay
}