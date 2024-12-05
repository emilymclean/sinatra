package cl.emilym.sinatra.data.client

import cl.emilym.sinatra.data.models.ShaDigest

abstract class EndpointDigestPair<T> {

    abstract val endpoint: suspend () -> T
    abstract val digest: suspend () -> ShaDigest

}