package cl.emilym.sinatra.data.models

import kotlinx.datetime.*
import java.time.temporal.ChronoUnit
import kotlin.test.*
import kotlin.time.Duration.Companion.days

class ServiceTest {

    private val tz = TimeZone.UTC
    private val now = Instant.parse("2024-04-29T12:00:00Z")

    private fun dateAt(hour: Int, minute: Int, dayOfWeek: DayOfWeek): Instant {
        val today = now.toLocalDateTime(tz).date
        val targetDate = today.toJavaLocalDate()
            .with(DayOfWeek.MONDAY)
            .plus((dayOfWeek.ordinal).toLong(), ChronoUnit.DAYS)
            .toKotlinLocalDate()
        return LocalDateTime(targetDate, LocalTime(hour, minute)).toInstant(tz)
    }

    @Test
    fun `active returns true if relevant regular and no removed exception`() {
        val instant = dateAt(12, 0, DayOfWeek.MONDAY)
        val regular = listOf(
            TimetableServiceRegular(true, false, false, false, false, false, false,
                instant - 1.days, instant + 1.days)
        )
        val service = Service("A", regular, emptyList())

        assertTrue(service.active(instant, tz))
    }

    @Test
    fun `active returns false if regular but removed exception`() {
        val instant = dateAt(12, 0, DayOfWeek.MONDAY)
        val regular = listOf(
            TimetableServiceRegular(true, false, false, false, false, false, false,
                instant - 1.days, instant + 1.days)
        )
        val exception = listOf(TimetableServiceException(instant, TimetableServiceExceptionType.REMOVED))
        val service = Service("A", regular, exception)

        assertFalse(service.active(instant, tz))
    }

    @Test
    fun `active returns true if exception added even if regular false`() {
        val instant = dateAt(12, 0, DayOfWeek.MONDAY)
        val regular = listOf(
            TimetableServiceRegular(false, false, false, false, false, false, false,
                instant - 1.days, instant + 1.days)
        )
        val exception = listOf(TimetableServiceException(instant, TimetableServiceExceptionType.ADDED))
        val service = Service("A", regular, exception)

        assertTrue(service.active(instant, tz))
    }

    @Test
    fun `TimetableServiceRegular is relevant on correct day and within range`() {
        val instant = dateAt(12, 0, DayOfWeek.TUESDAY)
        val regular = TimetableServiceRegular(
            monday = false,
            tuesday = true,
            wednesday = false,
            thursday = false,
            friday = false,
            saturday = false,
            sunday = false,
            startDate = instant - 1.days,
            endDate = instant + 1.days
        )

        assertTrue(regular.relevant(instant, tz))
    }

    @Test
    fun `TimetableServiceRegular is not relevant outside date range`() {
        val instant = dateAt(12, 0, DayOfWeek.TUESDAY)
        val regular = TimetableServiceRegular(
            monday = false,
            tuesday = true,
            wednesday = false,
            thursday = false,
            friday = false,
            saturday = false,
            sunday = false,
            startDate = instant - 3.days,
            endDate = instant - 2.days
        )

        assertFalse(regular.relevant(instant, tz))
    }

    @Test
    fun `TimetableServiceRegular is relevant if ignoreDates is true`() {
        val instant = dateAt(12, 0, DayOfWeek.TUESDAY)
        val regular = TimetableServiceRegular(
            monday = false,
            tuesday = true,
            wednesday = false,
            thursday = false,
            friday = false,
            saturday = false,
            sunday = false,
            startDate = instant + 10.days,
            endDate = instant + 11.days
        )

        assertTrue(regular.relevant(instant, tz, ignoreDates = true))
    }

    @Test
    fun `TimetableServiceException relevant within 1 day range`() {
        val instant = Clock.System.now()
        val exception = TimetableServiceException(instant, TimetableServiceExceptionType.ADDED)

        assertTrue(exception.relevant(instant))
        assertFalse(exception.relevant(instant + 2.days))
    }
}
