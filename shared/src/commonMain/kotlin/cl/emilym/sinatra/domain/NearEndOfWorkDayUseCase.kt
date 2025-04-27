package cl.emilym.sinatra.domain

import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.annotation.Factory

@Factory
class NearStartOfWorkDayUseCase(
    val clock: Clock
) {

    operator fun invoke(): Boolean {
        val time = clock.now().toLocalDateTime(TimeZone.currentSystemDefault())
        if (listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(time.dayOfWeek)) return false
        if (time.hour in 7..10) return true
        return false
    }

}