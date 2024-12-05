package cl.emilym.betterbuscanberra.data.client

import cl.emilym.betterbuscanberra.data.models.ShaDigest

interface EndpointDigestPair<T> {

    val endpoint: suspend () -> T
    val digest: suspend () -> ShaDigest

}