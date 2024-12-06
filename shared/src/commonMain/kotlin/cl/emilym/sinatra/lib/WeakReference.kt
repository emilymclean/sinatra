package cl.emilym.sinatra.lib

expect class NativeWeakReference<T: Any>(
    value: T
) {
    fun get(): T?
}