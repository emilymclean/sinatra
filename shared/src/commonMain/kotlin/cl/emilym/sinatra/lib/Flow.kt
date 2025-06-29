package cl.emilym.sinatra.lib

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.transformLatest
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalTypeInference::class, ExperimentalCoroutinesApi::class)
inline fun <reified T, R> combineTransformLatest(
    vararg flows: Flow<T>,
    @BuilderInference noinline transform: suspend FlowCollector<R>.(Array<T>) -> Unit
): Flow<R> = combine(*flows) { it }.transformLatest(transform)

@OptIn(ExperimentalTypeInference::class, ExperimentalCoroutinesApi::class)
fun <T1, T2, R> combineTransformLatest(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    @BuilderInference transform: suspend FlowCollector<R>.(T1, T2) -> Unit
): Flow<R> = combineTransformLatest(flow1, flow2) { args: Array<*> ->
    transform(
        args[0] as T1,
        args[1] as T2
    )
}

@OptIn(ExperimentalTypeInference::class, ExperimentalCoroutinesApi::class)
fun <T1, T2, T3, R> combineTransformLatest(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    @BuilderInference transform: suspend FlowCollector<R>.(T1, T2, T3) -> Unit
): Flow<R> = combineTransformLatest(flow1, flow2, flow3) { args: Array<*> ->
    transform(
        args[0] as T1,
        args[1] as T2,
        args[2] as T3,
    )
}

@OptIn(ExperimentalTypeInference::class, ExperimentalCoroutinesApi::class)
fun <T1, T2, T3, T4, R> combineTransformLatest(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    @BuilderInference transform: suspend FlowCollector<R>.(T1, T2, T3, T4) -> Unit
): Flow<R> = combineTransformLatest(flow1, flow2, flow3, flow4) { args: Array<*> ->
    transform(
        args[0] as T1,
        args[1] as T2,
        args[2] as T3,
        args[3] as T4
    )
}

@OptIn(ExperimentalTypeInference::class, ExperimentalCoroutinesApi::class)
fun <T1, T2, T3, T4, T5, R> combineTransformLatest(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    @BuilderInference transform: suspend FlowCollector<R>.(T1, T2, T3, T4, T5) -> Unit
): Flow<R> = combineTransformLatest(flow1, flow2, flow3, flow4, flow5) { args: Array<*> ->
    transform(
        args[0] as T1,
        args[1] as T2,
        args[2] as T3,
        args[3] as T4,
        args[4] as T5
    )
}

@OptIn(ExperimentalTypeInference::class, ExperimentalCoroutinesApi::class)
inline fun <reified T, R> combineFlatMapLatest(
    vararg flows: Flow<T>,
    @BuilderInference noinline transform: suspend (Array<T>) -> Flow<R>
): Flow<R> = combineTransformLatest(*flows) { emitAll(transform(it)) }

@OptIn(ExperimentalTypeInference::class, ExperimentalCoroutinesApi::class)
fun <T1, T2, R> combineFlatMapLatest(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    @BuilderInference transform: suspend (T1, T2) -> Flow<R>
): Flow<R> = combineFlatMapLatest(flow1, flow2) { args: Array<*> ->
    transform(
        args[0] as T1,
        args[1] as T2
    )
}

@OptIn(ExperimentalTypeInference::class, ExperimentalCoroutinesApi::class)
fun <T1, T2, T3, R> combineFlatMapLatest(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    @BuilderInference transform: suspend (T1, T2, T3) -> Flow<R>
): Flow<R> = combineFlatMapLatest(flow1, flow2, flow3) { args: Array<*> ->
    transform(
        args[0] as T1,
        args[1] as T2,
        args[2] as T3,
    )
}

@OptIn(ExperimentalTypeInference::class, ExperimentalCoroutinesApi::class)
fun <T1, T2, T3, T4, R> combineFlatMapLatest(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    @BuilderInference transform: suspend (T1, T2, T3, T4) -> Flow<R>
): Flow<R> = combineFlatMapLatest(flow1, flow2, flow3, flow4) { args: Array<*> ->
    transform(
        args[0] as T1,
        args[1] as T2,
        args[2] as T3,
        args[3] as T4
    )
}

@OptIn(ExperimentalTypeInference::class, ExperimentalCoroutinesApi::class)
fun <T1, T2, T3, T4, T5, R> combineFlatMapLatest(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    @BuilderInference transform: suspend (T1, T2, T3, T4, T5) -> Flow<R>
): Flow<R> = combineFlatMapLatest(flow1, flow2, flow3, flow4, flow5) { args: Array<*> ->
    transform(
        args[0] as T1,
        args[1] as T2,
        args[2] as T3,
        args[3] as T4,
        args[4] as T5
    )
}
