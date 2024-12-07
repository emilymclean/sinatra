package cl.emilym.sinatra

import io.github.aakira.napier.Napier

fun Napier.e(throwable: Throwable) {
    e(throwable.message ?: "Exception", throwable)
}

val Float.deg
    get() = this * 0.01745f