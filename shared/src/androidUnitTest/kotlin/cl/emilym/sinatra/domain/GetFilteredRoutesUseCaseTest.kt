package cl.emilym.sinatra.domain

import cl.emilym.sinatra.DefaultRoute
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.repository.Preference
import cl.emilym.sinatra.data.repository.PreferencesRepository
import cl.emilym.sinatra.data.repository.PreferencesUnit
import cl.emilym.sinatra.data.repository.RouteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GetFilteredRoutesUseCaseTest {

    private val routeRepository = mockk<RouteRepository>()
    private val preferencesRepository = mockk<PreferencesRepository>()
    private val useCase = GetFilteredRoutesUseCase(routeRepository, preferencesRepository)

    // Create test routes based on DefaultRoute
    private val regularRoute1 = DefaultRoute.copy(
        id = "route1",
        name = "Regular Route 1",
        schoolServiceOnly = false
    )

    private val regularRoute2 = DefaultRoute.copy(
        id = "route2",
        name = "Regular Route 2",
        schoolServiceOnly = false
    )

    private val schoolRoute1 = DefaultRoute.copy(
        id = "school1",
        name = "School Route 1",
        schoolServiceOnly = true
    )

    private val schoolRoute2 = DefaultRoute.copy(
        id = "school2",
        name = "School Route 2",
        schoolServiceOnly = true
    )

    private val allRoutes = listOf(regularRoute1, regularRoute2, schoolRoute1, schoolRoute2)
    private val nonSchoolRoutes = listOf(regularRoute1, regularRoute2)

    @Test
    fun `invoke should return all routes when ShowSchoolServices preference is true`() = runTest {
        // Given
        val cachableRoutes = Cachable.live(allRoutes)
        coEvery { routeRepository.routes() } returns cachableRoutes
        every { preferencesRepository.preference(Preference.ShowSchoolServices) } returns mockk<PreferencesUnit<Boolean>>().apply {
            every { flow } returns flowOf(true)
        }

        // When
        val result = useCase().toList()

        // Then
        assertEquals(1, result.size)
        assertEquals(allRoutes.filterAndSort(), result[0].item)
        coVerify { routeRepository.routes() }
        verify { preferencesRepository.preference(Preference.ShowSchoolServices) }
    }

    @Test
    fun `invoke should filter out school services when ShowSchoolServices preference is false`() = runTest {
        // Given
        val cachableRoutes = Cachable.live(allRoutes)
        coEvery { routeRepository.routes() } returns cachableRoutes
        every { preferencesRepository.preference(Preference.ShowSchoolServices) } returns mockk<PreferencesUnit<Boolean>>().apply {
            every { flow } returns flowOf(false)
        }

        // When
        val result = useCase().toList()

        // Then
        assertEquals(1, result.size)
        val filteredResult = result[0]
        assertEquals(nonSchoolRoutes.filterAndSort(), filteredResult.item)

        // Verify no school service routes are included
        filteredResult.item.forEach { route ->
            assertFalse(route.schoolServiceOnly, "Route ${route.name} should not be school service only")
        }

        coVerify { routeRepository.routes() }
        verify { preferencesRepository.preference(Preference.ShowSchoolServices) }
    }

    @Test
    fun `invoke should handle only school service routes when preference is false`() = runTest {
        // Given
        val schoolOnlyRoutes = listOf(schoolRoute1, schoolRoute2)
        val cachableRoutes = Cachable.live(schoolOnlyRoutes)
        coEvery { routeRepository.routes() } returns cachableRoutes
        every { preferencesRepository.preference(Preference.ShowSchoolServices) } returns mockk<PreferencesUnit<Boolean>>().apply {
            every { flow } returns flowOf(false)
        }

        // When
        val result = useCase().toList()

        // Then
        assertEquals(1, result.size)
        val filteredResult = result[0]
        assertTrue(filteredResult.item.isEmpty(), "All school service routes should be filtered out")
    }

    @Test
    fun `invoke should handle only regular routes when preference is false`() = runTest {
        // Given
        val regularOnlyRoutes = listOf(regularRoute1, regularRoute2)
        val cachableRoutes = Cachable.live(regularOnlyRoutes)
        coEvery { routeRepository.routes() } returns cachableRoutes
        every { preferencesRepository.preference(Preference.ShowSchoolServices) } returns mockk<PreferencesUnit<Boolean>>().apply {
            every { flow } returns flowOf(false)
        }

        // When
        val result = useCase().toList()

        // Then
        assertEquals(1, result.size)
        val filteredResult = result[0]
        assertEquals(regularOnlyRoutes.filterAndSort(), filteredResult.item)
        filteredResult.item.forEach { route ->
            assertFalse(route.schoolServiceOnly)
        }
    }

    @Test
    fun `invoke should react to preference changes`() = runTest {
        // Given
        val cachableRoutes = Cachable.live(allRoutes)
        coEvery { routeRepository.routes() } returns cachableRoutes
        every { preferencesRepository.preference(Preference.ShowSchoolServices) } returns mockk<PreferencesUnit<Boolean>>().apply {
            every { flow } returns flowOf(false, true, false)
        }

        // When
        val result = useCase().toList()

        // Then
        assertEquals(3, result.size)

        // First emission (preference = false) - filtered
        val firstResult = result[0]
        assertEquals(nonSchoolRoutes.filterAndSort(), firstResult.item)

        // Second emission (preference = true) - all routes
        val secondResult = result[1]
        assertEquals(allRoutes.filterAndSort(), secondResult.item)

        // Third emission (preference = false) - filtered again
        val thirdResult = result[2]
        assertEquals(nonSchoolRoutes.filterAndSort(), thirdResult.item)
    }

    @Test
    fun `filterRoutes should return all routes when ShowSchoolServices preference is true`() = runTest {
        // Given
        every { preferencesRepository.preference(Preference.ShowSchoolServices) } returns mockk<PreferencesUnit<Boolean>>().apply {
            every { flow } returns flowOf(true)
        }

        // When
        val result = useCase.filterRoutes(allRoutes).toList()

        // Then
        assertEquals(1, result.size)
        assertEquals(allRoutes.filterAndSort(), result[0])
    }

    @Test
    fun `filterRoutes should filter out school services when ShowSchoolServices preference is false`() = runTest {
        // Given
        every { preferencesRepository.preference(Preference.ShowSchoolServices) } returns mockk<PreferencesUnit<Boolean>>().apply {
            every { flow } returns flowOf(false)
        }

        // When
        val result = useCase.filterRoutes(allRoutes).toList()

        // Then
        assertEquals(1, result.size)
        assertEquals(nonSchoolRoutes.filterAndSort(), result[0])

        // Verify no school service routes are included
        result[0].forEach { route ->
            assertFalse(route.schoolServiceOnly, "Route ${route.name} should not be school service only")
        }
    }

    @Test
    fun `filterRoutes should handle empty routes list when preference is true`() = runTest {
        // Given
        every { preferencesRepository.preference(Preference.ShowSchoolServices) } returns mockk<PreferencesUnit<Boolean>>().apply {
            every { flow } returns flowOf(true)
        }

        // When
        val result = useCase.filterRoutes(emptyList()).toList()

        // Then
        assertEquals(1, result.size)
        assertTrue(result[0].isEmpty())
    }

    @Test
    fun `filterRoutes should handle empty routes list when preference is false`() = runTest {
        // Given
        every { preferencesRepository.preference(Preference.ShowSchoolServices) } returns mockk<PreferencesUnit<Boolean>>().apply {
            every { flow } returns flowOf(false)
        }

        // When
        val result = useCase.filterRoutes(emptyList()).toList()

        // Then
        assertEquals(1, result.size)
        assertTrue(result[0].isEmpty())
    }

    @Test
    fun `filterRoutes should react to preference changes`() = runTest {
        // Given
        every { preferencesRepository.preference(Preference.ShowSchoolServices) } returns mockk<PreferencesUnit<Boolean>>().apply {
            every { flow } returns flowOf(false, true, false)
        }

        // When
        val result = useCase.filterRoutes(allRoutes).toList()

        // Then
        assertEquals(3, result.size)

        // First emission (preference = false) - filtered
        assertEquals(nonSchoolRoutes.filterAndSort(), result[0])

        // Second emission (preference = true) - all routes
        assertEquals(allRoutes.filterAndSort(), result[1])

        // Third emission (preference = false) - filtered again
        assertEquals(nonSchoolRoutes.filterAndSort(), result[2])
    }
}