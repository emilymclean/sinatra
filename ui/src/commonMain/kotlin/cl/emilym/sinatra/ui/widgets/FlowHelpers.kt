package cl.emilym.sinatra.ui.widgets

import cl.emilym.compose.requeststate.RequestState
import cl.emilym.sinatra.e
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

fun <T> createRequestStateFlowFlow(): MutableStateFlow<Flow<RequestState<T>>> {
    return MutableStateFlow(flowOf(RequestState.Initial()))
}

fun <T> MutableStateFlow<Flow<RequestState<T>>>.presentable(): Flow<RequestState<T>> {
    return flatMapLatest { it.map { it } }
}

fun <T> createRequestStateFlow(): MutableStateFlow<RequestState<T>> {
    return MutableStateFlow(RequestState.Initial())
}

suspend fun <T> MutableStateFlow<Flow<RequestState<T>>>.handleFlowProperly(
    hideLoading: Boolean = false,
    operation: suspend () -> Flow<T>
) {
    if (!hideLoading) emit(flowOf(RequestState.Loading()))
    emit(
        operation().map<T, RequestState<T>> {
            RequestState.Success(it)
        }.catch {
            Napier.e(it)
            emit(RequestState.Failure(it as? Exception ?: Exception(it)))
        }
    )
}

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

fun <T> handleFlow(hideLoading: Boolean = false, handle: suspend () -> T): Flow<RequestState<T>> {
    return flow {
        if (!hideLoading) emit(RequestState.Loading())
        emit(
            try {
                RequestState.Success(handle())
            } catch (e: Exception) {
                RequestState.Failure(e)
            }
        )
    }
}