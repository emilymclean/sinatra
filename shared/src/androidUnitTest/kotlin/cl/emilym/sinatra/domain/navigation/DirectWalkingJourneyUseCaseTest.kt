package cl.emilym.sinatra.domain.navigation

import cl.emilym.sinatra.data.models.*
import cl.emilym.sinatra.data.repository.RoutingPreferencesRepository
import cl.emilym.sinatra.data.repository.TransportMetadataRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.minutes
import kotlin.time.times

class DirectWalkingJourneyUseCaseTest {

    private val routingPreferencesRepository = mockk<RoutingPreferencesRepository>()
    private val transportMetadataRepository = mockk<TransportMetadataRepository>()
    private val useCase = DirectWalkingJourneyUseCase(
        routingPreferencesRepository,
        transportMetadataRepository
    )

    private val locationA = JourneyLocation(
        location = MapLocation(0.0, 0.0),
        exact = true
    )

    private val locationB = JourneyLocation(
        location = MapLocation(0.0, 0.01), // ~1.11km apart
        exact = true
    )

    private val mockStartOfDay = Instant.parse("2024-01-01T00:00:00Z")

    @Test
    fun `returns null if walking time exceeds maximum`() = runTest {
        coEvery { routingPreferencesRepository.maximumWalkingTime() } returns 20.minutes
        coEvery { transportMetadataRepository.scheduleStartOfDay() } returns mockStartOfDay

        val result = useCase(
            locationA,
            locationB,
            JourneyCalculationTime.DepartureTime(Instant.parse("2024-01-01T12:00:00Z"))
        )

        assertNull(result)
    }

    @Test
    fun `returns correct Journey for departure time anchor`() = runTest {
        coEvery { routingPreferencesRepository.maximumWalkingTime() } returns 30.minutes
        coEvery { transportMetadataRepository.scheduleStartOfDay() } returns mockStartOfDay

        val anchorTime = Instant.parse("2024-01-01T12:00:00Z")
        val result = useCase(
            locationA,
            locationB,
            JourneyCalculationTime.DepartureTime(anchorTime)
        )

        val expectedDuration = distance(locationA.location, locationB.location) * 25.minutes
        val expectedDeparture = anchorTime
        val expectedArrival = anchorTime + expectedDuration

        val leg = result?.legs?.first() as JourneyLeg.TransferPoint
        assertEquals(expectedDuration, leg.travelTime)
        assertEquals(expectedDeparture.toTime(mockStartOfDay), leg.departureTime)
        assertEquals(expectedArrival.toTime(mockStartOfDay), leg.arrivalTime)
    }

    @Test
    fun `returns correct Journey for arrival time anchor`() = runTest {
        coEvery { routingPreferencesRepository.maximumWalkingTime() } returns 30.minutes
        coEvery { transportMetadataRepository.scheduleStartOfDay() } returns mockStartOfDay

        val anchorTime = Instant.parse("2024-01-01T12:00:00Z")
        val result = useCase(
            locationA,
            locationB,
            JourneyCalculationTime.ArrivalTime(anchorTime)
        )

        val expectedDuration = distance(locationA.location, locationB.location) * 25.minutes
        val expectedArrival = anchorTime
        val expectedDeparture = anchorTime - expectedDuration

        val leg = result?.legs?.first() as JourneyLeg.TransferPoint
        assertEquals(expectedDuration, leg.travelTime)
        assertEquals(expectedDeparture.toTime(mockStartOfDay), leg.departureTime)
        assertEquals(expectedArrival.toTime(mockStartOfDay), leg.arrivalTime)
    }
}
