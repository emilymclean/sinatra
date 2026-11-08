package cl.emilym.sinatra.domain.prompt

import cl.emilym.sinatra.FeatureFlag
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.CacheState
import cl.emilym.sinatra.data.models.IStopTimetableTime
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.Stop
import cl.emilym.sinatra.data.models.StopAccessibility
import cl.emilym.sinatra.data.models.StopTimetableTime
import cl.emilym.sinatra.data.models.StopVisibility
import cl.emilym.sinatra.data.models.StopWheelchairAccessibility
import cl.emilym.sinatra.data.models.StopWithDistance
import cl.emilym.sinatra.data.models.Time
import cl.emilym.sinatra.data.repository.FavouriteRepository
import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import cl.emilym.sinatra.domain.NearbyStopsUseCase
import cl.emilym.sinatra.domain.UpcomingRoutesForStopUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FavouriteNearbyStopDeparturesUseCaseTest {

    private val nearbyStopsUseCase = mockk<NearbyStopsUseCase>()
    private val favouriteRepository = mockk<FavouriteRepository>()
    private val upcomingRoutesForStopUseCase = mockk<UpcomingRoutesForStopUseCase>()
    private val remoteConfigRepository = mockk<RemoteConfigRepository>()

    private val useCase = FavouriteNearbyStopDeparturesUseCase(
        nearbyStopsUseCase,
        favouriteRepository,
        upcomingRoutesForStopUseCase,
        remoteConfigRepository
    )

    private val stop = Stop(
        id = "stop1",
        parentStation = null,
        name = "Test Stop",
        _simpleName = null,
        location = MapLocation(0.0, 0.0),
        accessibility = StopAccessibility(StopWheelchairAccessibility.UNKNOWN),
        visibility = StopVisibility(true, true, false, null),
        hasRealtime = false,
        schoolServiceOnly = false
    )
    private val stop2 = Stop(
        id = "stop2",
        parentStation = null,
        name = "Test Stop 2",
        _simpleName = null,
        location = MapLocation(0.001, 0.001),
        accessibility = StopAccessibility(StopWheelchairAccessibility.UNKNOWN),
        visibility = StopVisibility(true, true, false, null),
        hasRealtime = false,
        schoolServiceOnly = false
    )

    private val timetableTime = StopTimetableTime(
        childStopId = null,
        routeId = "route1",
        routeCode = "R1",
        serviceId = "service1",
        tripId = "trip1",
        arrivalTime = Time.parse("PT10H"),
        departureTime = Time.parse("PT10H"),
        heading = "North",
        sequence = 1,
        last = false,
        route = null,
        childStop = null
    )

    private val cachableDepartures = Cachable(listOf(timetableTime as IStopTimetableTime), CacheState.LIVE)

    @Test
    fun `should emit None if feature flag is disabled`() = runTest {
        coEvery { remoteConfigRepository.feature(FeatureFlag.FAVOURITE_NEARBY_STOP_HOME_SCREEN) } returns false
        coEvery { remoteConfigRepository.feature(FeatureFlag.FAVOURITE_NEARBY_STOP_HOME_SCREEN_MULTIPLE) } returns true

        val result = useCase(MapLocation(0.0, 0.0)).first()

        assertEquals(NearbyStopInformation.None, result)
    }

    @Test
    fun `should emit None if no favourite stop nearby`() = runTest {
        coEvery { remoteConfigRepository.feature(any()) } returns true
        coEvery { nearbyStopsUseCase(any(), any(), any()) } returns listOf(StopWithDistance(stop, 0.0))
        coEvery { favouriteRepository.favouritedStops(listOf(stop.id)) } returns flowOf(emptyList())

        val result = useCase(MapLocation(0.0, 0.0)).first()

        assertEquals(NearbyStopInformation.None, result)
    }

    @Test
    fun `should emit None if upcoming departures are empty`() = runTest {
        coEvery { remoteConfigRepository.feature(any()) } returns true
        coEvery { nearbyStopsUseCase(any(), any(), any()) } returns listOf(StopWithDistance(stop, 0.0))
        coEvery { favouriteRepository.favouritedStops(listOf(stop.id)) } returns flowOf(listOf(stop.id))
        coEvery { upcomingRoutesForStopUseCase(stop.id) } returns flowOf(Cachable(emptyList(), CacheState.LIVE))

        val result = useCase(MapLocation(0.0, 0.0)).first()

        assertEquals(NearbyStopInformation.None, result)
    }

    @Test
    fun `should emit Single if favourite nearby stop and upcoming departures exist`() = runTest {
        coEvery { remoteConfigRepository.feature(any()) } returns true
        coEvery { nearbyStopsUseCase(any(), any(), any()) } returns listOf(StopWithDistance(stop, 0.0))
        coEvery { favouriteRepository.favouritedStops(listOf(stop.id)) } returns flowOf(listOf(stop.id))
        coEvery { upcomingRoutesForStopUseCase(stop.id) } returns flowOf(cachableDepartures)

        val result = useCase(MapLocation(0.0, 0.0)).first()

        assertIs<NearbyStopInformation.Single>(result)
        assertEquals(stop, result.departures.stop)
        assertEquals(listOf(timetableTime), result.departures.departures)
    }

    @Test
    fun `should emit Multiple if multiple favourite nearby stops and multiple feature flag enabled`() = runTest {
        coEvery { remoteConfigRepository.feature(any()) } returns true
        coEvery { nearbyStopsUseCase(any(), any(), any()) } returns listOf(
            StopWithDistance(stop, 0.0),
            StopWithDistance(stop2, 0.1)
        )
        coEvery { favouriteRepository.favouritedStops(any()) } returns flowOf(listOf(stop.id, stop2.id))
        coEvery { upcomingRoutesForStopUseCase(any()) } returns flowOf(cachableDepartures)

        val result = useCase(MapLocation(0.0, 0.0)).first()

        assertIs<NearbyStopInformation.Multiple>(result)
        assertEquals(2, result.departures.size)
        assertEquals(stop, result.departures[0].stop)
        assertEquals(stop2, result.departures[1].stop)
    }

    @Test
    fun `should emit closest StopDepartures`() = runTest {
        coEvery { remoteConfigRepository.feature(any()) } returns true
        coEvery { remoteConfigRepository.feature(FeatureFlag.FAVOURITE_NEARBY_STOP_HOME_SCREEN_MULTIPLE) } returns false
        coEvery { nearbyStopsUseCase(any(), any(), any()) } returns listOf(
            StopWithDistance(stop, 0.0),
            StopWithDistance(stop2, 1.0),
        )
        coEvery { favouriteRepository.favouritedStops(any()) } returns flowOf(listOf(stop.id, stop2.id))
        coEvery { upcomingRoutesForStopUseCase(any()) } returns flowOf(cachableDepartures)

        val result = useCase(MapLocation(0.0, 0.0)).first()

        assertIs<NearbyStopInformation.Single>(result)
        assertEquals(stop, result.departures.stop)
        assertEquals(listOf(timetableTime), result.departures.departures)
    }
}
