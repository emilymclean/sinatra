package cl.emilym.sinatra.data.client

import cl.emilym.sinatra.data.models.Service
import cl.emilym.sinatra.data.models.ShaDigest
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import cl.emilym.sinatra.network.GtfsApi
import cl.emilym.gtfs.ServiceEndpoint
import org.koin.core.annotation.Factory
import pbandk.decodeFromByteArray

@Factory
class ServiceClient(
    private val gtfsApi: GtfsApi,
    private val transportMetadataRepository: TransportMetadataRepository
) {

    val servicesEndpointPair = object : EndpointDigestPair<List<Service>>("services") {
        override val endpoint = ::services
        override val digest = ::servicesDigest
    }

    suspend fun services(): List<Service> {
        val servicesPB = ServiceEndpoint.decodeFromByteArray(gtfsApi.services())
        return servicesPB.service.map { Service.fromPB(it, transportMetadataRepository.timeZone()) }
    }

    suspend fun servicesDigest(): ShaDigest {
        return gtfsApi.servicesDigest()
    }

}