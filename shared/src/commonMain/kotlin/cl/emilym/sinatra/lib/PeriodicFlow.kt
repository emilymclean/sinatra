package cl.emilym.sinatra.lib

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

fun periodicFlow(period: Duration = 1.minutes): Flow<Unit> = flow {
    while (currentCoroutineContext().isActive) {
        emit(Unit)
        delay(period)
    }
}