package cl.emilym.sinatra.domain

import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.Test

class NearWorkdayPeriodUseCaseTest {

    private val clock = mockk<Clock>()
    private val useCase = NearWorkdayPeriodUseCase(clock)

    private fun setMockedTime(localDateTime: LocalDateTime) {
        val instant = localDateTime.toInstant(TimeZone.currentSystemDefault())
        every { clock.now() } returns instant
    }

    @Test
    fun `should return all false on weekend`() {
        val saturday = LocalDateTime(2025, 4, 26, 10, 0) // Saturday
        setMockedTime(saturday)

        val result = useCase()

        assertEquals(false, result.inWorkPeriod)
        assertEquals(false, result.nearStart)
        assertEquals(false, result.nearEnd)
    }

    @Test
    fun `should be in work period during work hours`() {
        val workTime = LocalDateTime(2025, 4, 28, 11, 0) // Monday
        setMockedTime(workTime)

        val result = useCase()

        assertEquals(true, result.inWorkPeriod)
        assertEquals(false, result.nearStart)
        assertEquals(false, result.nearEnd)
    }

    @Test
    fun `should be near start before work hours`() {
        val nearStartTime = LocalDateTime(2025, 4, 28, 8, 0) // Monday
        setMockedTime(nearStartTime)

        val result = useCase()

        assertEquals(false, result.inWorkPeriod)
        assertEquals(true, result.nearStart)
        assertEquals(false, result.nearEnd)
    }

    @Test
    fun `should be near end after work hours`() {
        val nearEndTime = LocalDateTime(2025, 4, 28, 17, 30) // Monday
        setMockedTime(nearEndTime)

        val result = useCase()

        assertEquals(true, result.inWorkPeriod) // still in work period until 17
        assertEquals(false, result.nearStart)
        assertEquals(true, result.nearEnd)
    }

    @Test
    fun `should be near start and in work period around 9am`() {
        val overlapTime = LocalDateTime(2025, 4, 28, 9, 0) // Monday
        setMockedTime(overlapTime)

        val result = useCase()

        assertEquals(true, result.inWorkPeriod)
        assertEquals(true, result.nearStart)
        assertEquals(false, result.nearEnd)
    }

    @Test
    fun `should be in work period and near end around 16-17`() {
        val overlapEndTime = LocalDateTime(2025, 4, 28, 16, 30) // Monday
        setMockedTime(overlapEndTime)

        val result = useCase()

        assertEquals(true, result.inWorkPeriod)
        assertEquals(false, result.nearStart)
        assertEquals(true, result.nearEnd)
    }
}