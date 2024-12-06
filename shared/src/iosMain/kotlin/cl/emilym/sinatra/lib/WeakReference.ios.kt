package cl.emilym.sinatra.lib

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.WeakReference

actual class NativeWeakReference<T: Any> actual constructor(value: T) {
    @OptIn(ExperimentalNativeApi::class)
    private val value = WeakReference(value)

    @OptIn(ExperimentalNativeApi::class)
    actual fun get(): T? {
        return value.get()
    }
}