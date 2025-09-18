package cl.emilym.sinatra.data.client

import cl.emilym.sinatra.data.models.ShaDigest

abstract class BaseEndpointDigestPair<T> {
    abstract val digest: suspend () -> ShaDigest
}

abstract class EndpointDigestPair<T>: BaseEndpointDigestPair<T>() {

    abstract val endpoint: suspend () -> T

}

abstract class ValidatedEndpointDigestPair<T>: BaseEndpointDigestPair<T>() {
    abstract val endpoint: suspend (ShaDigest) -> T
}