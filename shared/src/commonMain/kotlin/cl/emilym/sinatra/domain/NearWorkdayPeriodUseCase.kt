package cl.emilym.sinatra.domain

import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.annotation.Factory

class WorkdayPeriodStatus(
    val inWorkPeriod: Boolean,
    val nearStart: Boolean,
    val nearEnd: Boolean
)

@Factory
class NearWorkdayPeriodUseCase(
    val clock: Clock
) {

    operator fun invoke(): WorkdayPeriodStatus {
        val time = clock.now().toLocalDateTime(TimeZone.currentSystemDefault())
        if (listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(time.dayOfWeek)) return WorkdayPeriodStatus(
            false, false, false
        )
        return WorkdayPeriodStatus(
            time.hour in 9..17,
            time.hour in 7..10,
            time.hour in 16..19
        )
    }

}