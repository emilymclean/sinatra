package cl.emilym.betterbuscanberra.data.client

import cl.emilym.betterbuscanberra.data.models.SearchResultChoice
import cl.emilym.betterbuscanberra.data.models.StopDetail
import org.koin.core.annotation.Factory

const val stopPOIClass = "StopLocation"

@Factory
class StopClient(
    val tripGoApi: TripGoApi
) {

    suspend fun findStop(nameOrId: String): List<Stop> {
        return tripGoApi.search(nameOrId).choices.toStopDetail()
    }

    private fun List<SearchResultChoice>.toStopDetail(): List<StopDetail> {
        filter {
            it.clazz == stopPOIClass
        }.map {
            StopDetail(
                it.stopCode!!,
                it.name,
                it.lat,
                it.lng,
                it.publicTransportMode!!,
                it.stopType!!
            )
        }
    }

}