package cl.emilym.sinatra.domain

import cl.emilym.sinatra.DefaultStop
import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.repository.Preference
import cl.emilym.sinatra.data.repository.PreferencesRepository
import cl.emilym.sinatra.data.repository.PreferencesUnit
import cl.emilym.sinatra.data.repository.StopRepository
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

class GetFilteredStopsUseCaseTest {

    private val stopRepository = mockk<StopRepository>()
    private val preferencesRepository = mockk<PreferencesRepository>()
    private val useCase = GetFilteredStopsUseCase(stopRepository, preferencesRepository)

    // Create test stops based on DefaultStop
    private val regularStop1 = DefaultStop.copy(
        id = "stop1",
        name = "Regular Stop 1",
        schoolServiceOnly = false
    )

    private val regularStop2 = DefaultStop.copy(
        id = "stop2",
        name = "Regular Stop 2",
        schoolServiceOnly = false
    )

    private val schoolStop1 = DefaultStop.copy(
        id = "school1",
        name = "School Stop 1",
        schoolServiceOnly = true
    )

    private val schoolStop2 = DefaultStop.copy(
        id = "school2",
        name = "School Stop 2",
        schoolServiceOnly = true
    )

    private val allStops = listOf(regularStop1, regularStop2, schoolStop1, schoolStop2)
    private val nonSchoolStops = listOf(regularStop1, regularStop2)

    @Test
    fun `invoke should return all stops when ShowSchoolServices preference is true`() = runTest {
        // Given
        val cachableStops = Cachable.live(allStops)
        coEvery { stopRepository.stops() } returns cachableStops
        every { preferencesRepository.preference(Preference.ShowSchoolServices) } returns mockk<PreferencesUnit<Boolean>>().apply {
            every { flow } returns flowOf(true)
        }

        // When
        val result = useCase().toList()

        // Then
        assertEquals(1, result.size)
        assertEquals(cachableStops, result[0])
        coVerify { stopRepository.stops() }
        verify { preferencesRepository.preference(Preference.ShowSchoolServices) }
    }

    @Test
    fun `invoke should filter out school services when ShowSchoolServices preference is false`() = runTest {
        // Given
        val cachableStops = Cachable.live(allStops)
        coEvery { stopRepository.stops() } returns cachableStops
        every { preferencesRepository.preference(Preference.ShowSchoolServices) } returns mockk<PreferencesUnit<Boolean>>().apply {
            every { flow } returns flowOf(false)
        }

        // When
        val result = useCase().toList()

        // Then
        assertEquals(1, result.size)
        val filteredResult = result[0]
        assertEquals(nonSchoolStops, filteredResult.item)

        // Verify no school service stops are included
        filteredResult.item.forEach { stop ->
            assertFalse(stop.schoolServiceOnly, "Stop ${stop.name} should not be school service only")
        }

        coVerify { stopRepository.stops() }
        verify { preferencesRepository.preference(Preference.ShowSchoolServices) }
    }

    @Test
    fun `invoke should handle only school service stops when preference is false`() = runTest {
        // Given
        val schoolOnlyStops = listOf(schoolStop1, schoolStop2)
        val cachableStops = Cachable.live(schoolOnlyStops)
        coEvery { stopRepository.stops() } returns cachableStops
        every { preferencesRepository.preference(Preference.ShowSchoolServices) } returns mockk<PreferencesUnit<Boolean>>().apply {
            every { flow } returns flowOf(false)
        }

        // When
        val result = useCase().toList()

        // Then
        assertEquals(1, result.size)
        val filteredResult = result[0]
        assertTrue(filteredResult.item.isEmpty(), "All school service stops should be filtered out")
    }

    @Test
    fun `invoke should handle only regular stops when preference is false`() = runTest {
        // Given
        val regularOnlyStops = listOf(regularStop1, regularStop2)
        val cachableStops = Cachable.live(regularOnlyStops)
        coEvery { stopRepository.stops() } returns cachableStops
        every { preferencesRepository.preference(Preference.ShowSchoolServices) } returns mockk<PreferencesUnit<Boolean>>().apply {
            every { flow } returns flowOf(false)
        }

        // When
        val result = useCase().toList()

        // Then
        assertEquals(1, result.size)
        val filteredResult = result[0]
        assertEquals(regularOnlyStops, filteredResult.item)
        filteredResult.item.forEach { stop ->
            assertFalse(stop.schoolServiceOnly)
        }
    }

    @Test
    fun `invoke should react to preference changes`() = runTest {
        // Given
        val cachableStops = Cachable.live(allStops)
        coEvery { stopRepository.stops() } returns cachableStops
        every { preferencesRepository.preference(Preference.ShowSchoolServices) } returns mockk<PreferencesUnit<Boolean>>().apply {
            every { flow } returns flowOf(false, true, false)
        }

        // When
        val result = useCase().toList()

        // Then
        assertEquals(3, result.size)

        // First emission (preference = false) - filtered
        val firstResult = result[0]
        assertEquals(nonSchoolStops, firstResult.item)

        // Second emission (preference = true) - all stops
        val secondResult = result[1]
        assertEquals(allStops, secondResult.item)

        // Third emission (preference = false) - filtered again
        val thirdResult = result[2]
        assertEquals(nonSchoolStops, thirdResult.item)
    }
}