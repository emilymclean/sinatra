package cl.emilym.betterbuscanberra.data.client

import cl.emilym.betterbuscanberra.data.models.StopCode
import cl.emilym.betterbuscanberra.data.models.StopService
import cl.emilym.betterbuscanberra.data.models.TimetableRequest
import cl.emilym.betterbuscanberra.data.models.TimetableResponse
import org.koin.core.annotation.Factory

@Factory
class ServiceClient(
    private val tripGoApi: TripGoApi
) {

    suspend fun timetable(stop: StopCode): List<StopService> {
        return tripGoApi.timetable(
            TimetableRequest(
                embarkationStops = listOf(stop)
            )
        ).embarkationStops.first().services
    }

}