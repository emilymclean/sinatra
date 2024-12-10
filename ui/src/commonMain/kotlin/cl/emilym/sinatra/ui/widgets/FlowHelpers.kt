package cl.emilym.sinatra.ui.widgets

import cl.emilym.compose.requeststate.RequestState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map

suspend fun <T> MutableStateFlow<RequestState<T>>.handleFlow(hideLoading: Boolean = false, operation: suspend () -> Flow<T>) {
    if (!hideLoading) emit(RequestState.Loading())
    try {
        emitAll(operation().catch {
            RequestState.Failure<T>(it as? Exception ?: Exception(it))
        }.map {
            RequestState.Success(it)
        })
    } catch (e: Exception) {
        emit(RequestState.Failure(e))
    }
}