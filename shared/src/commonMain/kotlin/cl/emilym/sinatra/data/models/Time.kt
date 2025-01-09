package cl.emilym.sinatra.data.models

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration

interface Time: Comparable<Any> {

    val instant: Instant
        get() { throw IllegalStateException("Time has no reference day") }
    val durationThroughDay: Duration

    fun addReference(startOfDay: Instant): Time
    fun forDay(startOfDay: Instant): Time

    infix operator fun <T> plus(other: T): Time
    infix operator fun <T> minus(other: T): Time

    companion object {
        fun create(durationThroughDay: Duration, startOfDay: Instant? = null): Time {
            return when {
                startOfDay == null -> UnreferencedTime(durationThroughDay)
                else -> DefaultReferencedTime(durationThroughDay, startOfDay)
            }
        }

        fun parse(time: String) = create(Duration.parseIsoString(time))
    }
}

interface ReferencedTime: Time {
    val startOfDay: Instant
}

abstract class AbstractTime: Time {

    override fun addReference(startOfDay: Instant): Time  {
        return DefaultReferencedTime(durationThroughDay, startOfDay)
    }

    override fun forDay(startOfDay: Instant): Time {
        return DefaultReferencedTime(durationThroughDay, startOfDay)
    }

    override infix operator fun <T> plus(other: T): Time {
        throw IllegalStateException("Cannot do maths on unreferenced time")
    }
    override infix operator fun <T> minus(other: T): Time {
        throw IllegalStateException("Cannot do maths on unreferenced time")
    }

    override fun compareTo(other: Any): Int {
        return when (other) {
            is Instant -> instant.compareTo(other)
            is Time -> when {
                this is ReferencedTime && other is ReferencedTime -> instant.compareTo(other.instant)
                this !is ReferencedTime && other !is ReferencedTime -> durationThroughDay.compareTo(other.durationThroughDay)
                else -> throw IllegalArgumentException("Cannot compare referenced and unreferenced times")
            }
            else -> throw IllegalArgumentException("Cannot compare Time and ${other::class.simpleName}")
        }
    }

}

internal data class UnreferencedTime(
    override val durationThroughDay: Duration
): AbstractTime()

internal data class DefaultReferencedTime(
    override val durationThroughDay: Duration,
    override val startOfDay: Instant
): AbstractTime(), ReferencedTime {

    override val instant: Instant
        get() = startOfDay + durationThroughDay

    override fun addReference(startOfDay: Instant) = this

    override infix operator fun <T> plus(other: T): Time {
        return when (other) {
            is Duration -> (instant + other).toTime(startOfDay)
            else -> throw IllegalArgumentException("Cannot add Time and ${
                if (other == null) null else other!!::class.simpleName
            }")
        }
    }

    override infix operator fun <T> minus(other: T): Time {
        return when (other) {
            is Duration -> (instant - other).toTime(startOfDay)
            else -> throw IllegalArgumentException("Cannot subtract Time and ${
                if (other == null) null else other!!::class.simpleName
            }")
        }
    }

}

fun Clock.startOfDay(timeZone: TimeZone): Instant {
    return now().startOfDay(timeZone)
}

fun Instant.startOfDay(timeZone: TimeZone): Instant {
    val inTz = toLocalDateTime(timeZone)
    return LocalDateTime(inTz.year, inTz.month, inTz.dayOfMonth, 0, 0, 0, 0).toInstant(timeZone)
}

fun LocalDateTime.isSameDay(other: LocalDateTime): Boolean {
    return year == other.year && month == other.month && dayOfMonth == other.dayOfMonth
}

fun Instant.toTodayTime(timeZone: TimeZone): ReferencedTime {
    val startOfDay = startOfDay(timeZone)
    val durationThroughDay = this - startOfDay
    return Time.create(durationThroughDay, startOfDay) as ReferencedTime
}

fun Instant.toTime(startOfDay: Instant): Time {
    val durationThroughDay = this - startOfDay
    return Time.create(durationThroughDay, startOfDay)
}

@Deprecated("Use parse constructor", ReplaceWith("Time.parse(time)"))
fun parseTime(time: String): Time {
    return Time.parse(time)
}