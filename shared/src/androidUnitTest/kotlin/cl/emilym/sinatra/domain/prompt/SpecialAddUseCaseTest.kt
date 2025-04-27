package cl.emilym.sinatra.domain.prompt

import cl.emilym.sinatra.data.models.Favourite
import cl.emilym.sinatra.data.models.SpecialFavouriteType
import cl.emilym.sinatra.data.repository.FavouriteRepository
import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SpecialAddUseCaseTest {

    private val favouriteRepository = mockk<FavouriteRepository>()
    private val remoteConfigRepository = mockk<RemoteConfigRepository>()

    private val useCase = SpecialAddUseCase(
        favouriteRepository,
        remoteConfigRepository
    )

    @Test
    fun `should emit empty list if feature flag is disabled`() = runTest {
        coEvery { remoteConfigRepository.feature(SpecialAddUseCase.QUICK_ADD_NAVIGATION_FEATURE_FLAG) } returns false

        val result = useCase().first()

        assertEquals(emptyList(), result)
    }

    @Test
    fun `should emit all special types when user has none favourited`() = runTest {
        coEvery { remoteConfigRepository.feature(SpecialAddUseCase.QUICK_ADD_NAVIGATION_FEATURE_FLAG) } returns true
        coEvery { favouriteRepository.all() } returns flowOf(emptyList())

        val result = useCase().first()

        assertEquals(SpecialFavouriteType.entries, result)
    }

    @Test
    fun `should emit only unfavourited special types`() = runTest {
        coEvery { remoteConfigRepository.feature(SpecialAddUseCase.QUICK_ADD_NAVIGATION_FEATURE_FLAG) } returns true
        coEvery { favouriteRepository.all() } returns flowOf(
            listOf(
                Favourite.Place(
                    place = mockk(),
                    specialType = SpecialFavouriteType.HOME
                )
            )
        )

        val result = useCase().first()

        assertEquals(listOf(SpecialFavouriteType.WORK), result)
    }

    @Test
    fun `should emit no special types if all are already favourited`() = runTest {
        coEvery { remoteConfigRepository.feature(SpecialAddUseCase.QUICK_ADD_NAVIGATION_FEATURE_FLAG) } returns true
        coEvery { favouriteRepository.all() } returns flowOf(
            listOf(
                Favourite.Place(
                    place = mockk(),
                    specialType = SpecialFavouriteType.HOME
                ),
                Favourite.Place(
                    place = mockk(),
                    specialType = SpecialFavouriteType.WORK
                )
            )
        )

        val result = useCase().first()

        assertEquals(emptyList(), result)
    }
}
