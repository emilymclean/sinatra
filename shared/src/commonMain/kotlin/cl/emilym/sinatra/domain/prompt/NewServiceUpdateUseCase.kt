package cl.emilym.sinatra.domain.prompt

import cl.emilym.sinatra.FeatureFlag
import cl.emilym.sinatra.data.models.ServiceAlert
import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import cl.emilym.sinatra.data.repository.ServiceAlertRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.datetime.Clock
import org.koin.core.annotation.Factory
import kotlin.time.Duration.Companion.days

@Factory
class NewServiceUpdateUseCase(
    private val serviceAlertRepository: ServiceAlertRepository,
    private val remoteConfigRepository: RemoteConfigRepository,
    private val clock: Clock
) {

    companion object {
        private val NEW_ALERT_CUTOFF = 2.days
        const val NEW_SERVICE_FEATURE_FLAG = "new_service_home_screen"
    }

    operator fun invoke(): Flow<List<ServiceAlert>> {
        return flow {
            val now = clock.now()
            if (!remoteConfigRepository.feature(FeatureFlag.NEW_SERVICE_HOME_SCREEN)) {
                emit(listOf())
                return@flow
            }

            emitAll(
                serviceAlertRepository.alertsLive().mapLatest {
                    it.filter { serviceAlert ->
                        serviceAlert.date?.let {
                            now - it < (serviceAlert.highlightDuration ?: NEW_ALERT_CUTOFF)
                        } ?: false && !serviceAlert.viewed
                    }
                }
            )
        }
    }

}