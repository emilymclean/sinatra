package cl.emilym.sinatra.data.repository

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

class LiveServiceRepository {

    private val timerFlow get() = flow {
        while (currentCoroutineContext().isActive) {
            emit(Unit)
        }
    }

    fun getRealtimeUpdates() {

    }

}