package cl.emilym.sinatra.domain

import cl.emilym.sinatra.BuildInformation
import cl.emilym.sinatra.data.persistence.ContentPersistence
import cl.emilym.sinatra.data.repository.AppRepository
import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import cl.emilym.sinatra.e
import io.github.aakira.napier.Napier
import io.mockk.*
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class ForceRefreshRemoteConfigOnUpdateUseCaseTest {

    private val remoteConfigRepository = mockk<RemoteConfigRepository>(relaxed = true)
    private val appRepository = mockk<AppRepository>(relaxed = true)
    private val build = BuildInformation(versionName = "1.0.0", versionNumber = "2")
    private val contentPeristence = mockk<ContentPersistence>(relaxed = true)

    private lateinit var useCase: ForceRefreshRemoteConfigOnUpdateUseCase

    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this)
        useCase = ForceRefreshRemoteConfigOnUpdateUseCase(remoteConfigRepository, appRepository, contentPeristence, build)
    }

    @Test
    fun `does nothing if app version has not changed`() = runTest {
        coEvery { appRepository.lastAppCode() } returns 2

        useCase()

        coVerify(exactly = 0) { remoteConfigRepository.forceReload() }
        coVerify(exactly = 0) { appRepository.setLastAppCode(any()) }
        coVerify(exactly = 0) { contentPeristence.clearCache() }
    }

    @Test
    fun `reloads remote config and updates app code if version changed`() = runTest {
        coEvery { appRepository.lastAppCode() } returns 1

        useCase()

        coVerify(exactly = 1) { remoteConfigRepository.forceReload() }
        coVerify(exactly = 1) { appRepository.setLastAppCode(2) }
        coVerify(exactly = 1) { contentPeristence.clearCache() }
    }

    @Test
    fun `logs error and does not update app code if forceReload fails`() = runTest {
        coEvery { appRepository.lastAppCode() } returns 1
        coEvery { remoteConfigRepository.forceReload() } throws RuntimeException("network error")

        mockkObject(Napier)
        coEvery { Napier.e(any<Throwable>()) } just Runs

        useCase()

        coVerify(exactly = 1) { remoteConfigRepository.forceReload() }
        coVerify(exactly = 0) { appRepository.setLastAppCode(any()) }
        coVerify(exactly = 0) { contentPeristence.clearCache() }
        verify { Napier.e(any(), any()) }

        unmockkObject(Napier)
    }
}
