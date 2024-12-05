package cl.emilym.sinatra.data.client

import cl.emilym.sinatra.data.models.ShaDigest

interface EndpointDigestPair<T> {

    val endpoint: suspend () -> T
    val digest: suspend () -> ShaDigest

}