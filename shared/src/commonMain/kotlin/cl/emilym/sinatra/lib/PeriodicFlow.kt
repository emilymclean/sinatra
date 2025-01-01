package cl.emilym.sinatra.lib

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

fun periodicFlow(period: Duration = 1.minutes): Flow<Unit> = callbackFlow {
    val job = launch {
        while (true) {
            send(Unit)
            delay(period)
        }
    }

    awaitClose { job.cancel() }
}