package cl.emilym.sinatra.domain.prompt

import cl.emilym.sinatra.data.models.Favourite
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.SpecialFavouriteType
import cl.emilym.sinatra.data.repository.FavouriteRepository
import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import cl.emilym.sinatra.domain.NearWorkdayPeriodUseCase
import cl.emilym.sinatra.domain.WorkdayPeriodStatus
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class QuickNavigateUseCaseTest {

    private val favouriteRepository = mockk<FavouriteRepository>()
    private val remoteConfigRepository = mockk<RemoteConfigRepository>()
    private val nearWorkdayPeriodUseCase = mockk<NearWorkdayPeriodUseCase>()

    private val useCase = QuickNavigateUseCase(
        favouriteRepository,
        remoteConfigRepository,
        nearWorkdayPeriodUseCase
    )

    private val homeLocation = MapLocation(lat = 0.0, lng = 0.0)
    private val workLocation = MapLocation(lat = 10.0, lng = 10.0)

    private val homeStop = Favourite.Stop(
        stop = mockk {
            coEvery { location } returns homeLocation
        },
        specialType = SpecialFavouriteType.HOME
    )

    private val workPlace = Favourite.Place(
        place = mockk {
            coEvery { location } returns workLocation
        },
        specialType = SpecialFavouriteType.WORK
    )

    @Test
    fun `should emit empty list if feature flag is disabled`() = runTest {
        coEvery { remoteConfigRepository.feature(QuickNavigateUseCase.QUICK_NAVIGATION_FEATURE_FLAG) } returns false

        val result = useCase(currentLocation = null).first()

        assertEquals(emptyList(), result)
    }

    @Test
    fun `should emit favourites sorted with WORK first if near start of work day`() = runTest {
        coEvery { remoteConfigRepository.feature(QuickNavigateUseCase.QUICK_NAVIGATION_FEATURE_FLAG) } returns true
        coEvery { favouriteRepository.all() } returns flowOf(listOf(homeStop, workPlace))
        coEvery { nearWorkdayPeriodUseCase() } returns WorkdayPeriodStatus(
            inWorkPeriod = false,
            nearStart = true,
            nearEnd = false
        )

        val result = useCase(currentLocation = null).first()

        // WORK should come before HOME when near start of work day
        assertEquals(
            listOf(
                QuickNavigation(
                    navigation = workPlace.place,
                    specialType = SpecialFavouriteType.WORK,
                    important = false
                ),
                QuickNavigation(
                    navigation = homeStop.stop,
                    specialType = SpecialFavouriteType.HOME,
                    important = false
                )
            ),
            result
        )
    }

    @Test
    fun `should emit favourites sorted with HOME first if not near start of work day`() = runTest {
        coEvery { remoteConfigRepository.feature(QuickNavigateUseCase.QUICK_NAVIGATION_FEATURE_FLAG) } returns true
        coEvery { favouriteRepository.all() } returns flowOf(listOf(homeStop, workPlace))
        coEvery { nearWorkdayPeriodUseCase() } returns WorkdayPeriodStatus(
            inWorkPeriod = true,
            nearStart = false,
            nearEnd = false
        )

        val result = useCase(currentLocation = null).first()

        // HOME should come before WORK when not near start of work day
        assertEquals(
            listOf(
                QuickNavigation(
                    navigation = homeStop.stop,
                    specialType = SpecialFavouriteType.HOME,
                    important = false
                ),
                QuickNavigation(
                    navigation = workPlace.place,
                    specialType = SpecialFavouriteType.WORK,
                    important = false
                )
            ),
            result
        )
    }

    @Test
    fun `should filter out favourites that are too close to current location`() = runTest {
        val currentLocation = MapLocation(lat = 0.0, lng = 0.0) // Same as `homeLocation`

        coEvery { remoteConfigRepository.feature(QuickNavigateUseCase.QUICK_NAVIGATION_FEATURE_FLAG) } returns true
        coEvery { favouriteRepository.all() } returns flowOf(listOf(homeStop, workPlace))
        coEvery { nearWorkdayPeriodUseCase() } returns WorkdayPeriodStatus(
            inWorkPeriod = false,
            nearStart = true,
            nearEnd = false
        )

        val result = useCase(currentLocation).first()

        // Home should be filtered out because it's "too close", work should remain
        assertEquals(
            listOf(
                QuickNavigation(
                    navigation = workPlace.place,
                    specialType = SpecialFavouriteType.WORK,
                    important = false
                )
            ),
            result
        )
    }
}
