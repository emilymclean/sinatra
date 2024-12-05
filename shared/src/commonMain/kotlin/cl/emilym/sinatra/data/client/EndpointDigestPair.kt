package cl.emilym.sinatra.data.client

import cl.emilym.sinatra.data.models.ResourceKey
import cl.emilym.sinatra.data.models.ShaDigest

abstract class EndpointDigestPair<T>(
    val resource: ResourceKey
) {

    abstract val endpoint: suspend () -> T
    abstract val digest: suspend () -> ShaDigest

}