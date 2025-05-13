package cl.emilym.sinatra.domain.navigation

import cl.emilym.sinatra.data.models.Journey
import cl.emilym.sinatra.data.models.JourneyLeg
import cl.emilym.sinatra.data.models.distance
import cl.emilym.sinatra.data.models.toTime
import cl.emilym.sinatra.data.repository.RoutingPreferencesRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import org.koin.core.annotation.Factory
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.times

@Factory
class DirectWalkingJourneyUseCase(
    private val routingPreferencesRepository: RoutingPreferencesRepository,
    private val transportMetadataRepository: TransportMetadataRepository
) {

    suspend operator fun invoke(
        departureLocation: JourneyLocation,
        arrivalLocation: JourneyLocation,
        anchorTime: JourneyCalculationTime,
        assumedWalkingTimePerKilometer: Duration = 25.minutes,
    ): Journey? {
        val maximumWalkingTime = routingPreferencesRepository.maximumWalkingTime()
        val walkingTime = distance(departureLocation.location, arrivalLocation.location) *
                assumedWalkingTimePerKilometer
        if (walkingTime > maximumWalkingTime) return null

        val startOfDay = transportMetadataRepository.scheduleStartOfDay()
        val departureTime = when (anchorTime) {
            is JourneyCalculationTime.DepartureTime -> anchorTime.time
            is JourneyCalculationTime.ArrivalTime -> anchorTime.time - walkingTime
        }
        val arrivalTime = when (anchorTime) {
            is JourneyCalculationTime.ArrivalTime -> anchorTime.time
            is JourneyCalculationTime.DepartureTime -> anchorTime.time + walkingTime
        }

        return Journey(
            listOf(
                JourneyLeg.TransferPoint(
                    walkingTime,
                    departureTime.toTime(startOfDay),
                    arrivalTime.toTime(startOfDay)
                )
            )
        )
    }

}