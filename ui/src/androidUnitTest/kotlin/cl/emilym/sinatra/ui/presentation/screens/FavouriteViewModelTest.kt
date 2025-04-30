package cl.emilym.sinatra.ui.presentation.screens

import cl.emilym.sinatra.data.models.Favourite
import cl.emilym.sinatra.data.models.SpecialFavouriteType
import cl.emilym.sinatra.data.repository.FavouriteRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FavouriteViewModelTest {

    private lateinit var repository: FavouriteRepository
    private lateinit var viewModel: FavouriteViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val favouritesFlow = MutableStateFlow<List<Favourite>>(emptyList())

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        repository = mockk(relaxed = true)

        coEvery { repository.all() } returns favouritesFlow

        viewModel = FavouriteViewModel(repository)
    }

    @Test
    fun `initial state should be Favourite`() = runTest {
        assertEquals(FavouriteState.Favourite, viewModel.state.value)
    }

    @Test
    fun `openSearch should update state to Search`() = runTest {
        favouritesFlow.value = listOf(
            Favourite.Stop(
                stop = mockk(relaxed = true),
                specialType = SpecialFavouriteType.HOME
            )
        )
        viewModel.special.first { it.isNotEmpty() }
        viewModel.openSearch(SpecialFavouriteType.HOME)

        val result = viewModel.state.first { it is FavouriteState.Search }

        assertIs<FavouriteState.Search>(result)
        assertEquals(SpecialFavouriteType.HOME, result.type)
        assertTrue(result.hasExisting)
    }

    @Test
    fun `closeSearch resets state to Favourite`() = runTest {
        viewModel.openSearch(SpecialFavouriteType.HOME)
        viewModel.closeSearch()
        assertEquals(FavouriteState.Favourite, viewModel.state.value)
    }

    @Test
    fun `anyFavourites reflects whether favourites are non-empty`() = runTest {
        assertTrue(viewModel.anyFavourites.first { it }) // default is true

        favouritesFlow.value = emptyList()
        assertFalse(viewModel.anyFavourites.first { !it }) // should be false

        favouritesFlow.value = listOf(
            Favourite.Route(mockk(relaxed = true))
        )
        assertTrue(viewModel.anyFavourites.first { it })
    }

    @Test
    fun `special favourites are populated correctly`() = runTest {
        val stop = Favourite.Stop(mockk(relaxed = true), specialType = SpecialFavouriteType.WORK)
        val place = Favourite.Place(mockk(relaxed = true), specialType = SpecialFavouriteType.HOME)

        favouritesFlow.value = listOf(stop, place)

        val result = viewModel.special.first { it.any { it.favourite != null } }

        assertEquals(2, result.size)
        assertEquals(SpecialFavouriteType.HOME, result[0].type)
        assertEquals(place, result[0].favourite)
        assertEquals(SpecialFavouriteType.WORK, result[1].type)
        assertEquals(stop, result[1].favourite)
    }

}
