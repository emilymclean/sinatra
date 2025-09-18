package cl.emilym.sinatra.data.client

import cl.emilym.sinatra.data.models.ServiceAlert
import cl.emilym.sinatra.data.models.ShaDigest
import cl.emilym.sinatra.network.GtfsApi
import cl.emilym.sinatra.network.validated
import org.koin.core.annotation.Factory

@Factory
class ServiceAlertClient(
    val gtfsApi: GtfsApi
) {

    val serviceAlertsPair by lazy {
        object : ValidatedEndpointDigestPair<List<ServiceAlert>>() {
            override val endpoint = ::serviceAlerts
            override val digest = ::serviceAlertsDigest
        }
    }

    suspend fun serviceAlerts(digest: ShaDigest): List<ServiceAlert> {
        return gtfsApi.serviceAlerts().validated(digest).alerts.map { ServiceAlert.fromPB(it) }
    }

    suspend fun serviceAlertsDigest(): ShaDigest {
        return gtfsApi.serviceAlertsDigest()
    }

}