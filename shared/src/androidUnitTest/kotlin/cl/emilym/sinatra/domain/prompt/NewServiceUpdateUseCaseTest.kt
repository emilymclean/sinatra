package cl.emilym.sinatra.domain.prompt

import cl.emilym.sinatra.data.models.Cachable
import cl.emilym.sinatra.data.models.ServiceAlert
import cl.emilym.sinatra.data.models.ServiceAlertRegion
import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import cl.emilym.sinatra.data.repository.ServiceAlertRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days

class NewServiceUpdateUseCaseTest {

    private val serviceAlertRepository = mockk<ServiceAlertRepository>()
    private val remoteConfigRepository = mockk<RemoteConfigRepository>()
    private val clock = mockk<Clock>()

    private val useCase = NewServiceUpdateUseCase(
        serviceAlertRepository,
        remoteConfigRepository,
        clock
    )

    private val now = Instant.parse("2025-04-27T10:00:00Z")

    private val recentAlert = ServiceAlert(
        id = "1",
        title = "Recent Alert",
        url = null,
        date = now - 1.days,
        regions = listOf(ServiceAlertRegion.CENTRAL_CANBERRA),
        highlightDuration = 2.days
    )

    private val oldAlert = ServiceAlert(
        id = "2",
        title = "Old Alert",
        url = null,
        date = now - 10.days,
        regions = listOf(ServiceAlertRegion.BELCONNEN),
        highlightDuration = 2.days
    )

    private val noDateAlert = ServiceAlert(
        id = "3",
        title = "No Date Alert",
        url = null,
        date = null,
        regions = listOf(ServiceAlertRegion.OTHER),
        highlightDuration = 2.days
    )

    private val viewedAlert = ServiceAlert(
        id = "3",
        title = "No Date Alert",
        url = null,
        date = null,
        regions = listOf(ServiceAlertRegion.OTHER),
        highlightDuration = 2.days,
        viewed = true
    )

    private val allAlerts = listOf(recentAlert, oldAlert, noDateAlert)

    @Test
    fun `should emit empty list if feature flag is disabled`() = runTest {
        coEvery { clock.now() } returns now
        coEvery { remoteConfigRepository.feature(NewServiceUpdateUseCase.NEW_SERVICE_FEATURE_FLAG) } returns false

        val result = useCase().first()

        assertEquals(emptyList(), result)
    }

    @Test
    fun `should filter only recent alerts`() = runTest {
        coEvery { clock.now() } returns now
        coEvery { remoteConfigRepository.feature(NewServiceUpdateUseCase.NEW_SERVICE_FEATURE_FLAG) } returns true
        coEvery { serviceAlertRepository.alerts() } returns Cachable.live(allAlerts)

        val result = useCase().first()

        assertEquals(listOf(recentAlert), result)
    }

    @Test
    fun `should filter out viewed alerts`() = runTest {
        coEvery { clock.now() } returns now
        coEvery { remoteConfigRepository.feature(NewServiceUpdateUseCase.NEW_SERVICE_FEATURE_FLAG) } returns true
        coEvery { serviceAlertRepository.alerts() } returns Cachable.live(listOf(recentAlert, viewedAlert))

        val result = useCase().first()

        assertEquals(listOf(recentAlert), result)
    }

    @Test
    fun `should emit empty list when no alerts are recent`() = runTest {
        coEvery { clock.now() } returns now
        coEvery { remoteConfigRepository.feature(NewServiceUpdateUseCase.NEW_SERVICE_FEATURE_FLAG) } returns true
        coEvery { serviceAlertRepository.alerts() } returns Cachable.live(listOf(oldAlert, noDateAlert))

        val result = useCase().first()

        assertEquals(emptyList(), result)
    }
}
