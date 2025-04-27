package cl.emilym.sinatra.domain.smart

import cl.emilym.sinatra.data.models.ServiceAlert
import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import cl.emilym.sinatra.data.repository.ServiceAlertRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
        private val NEW_ALERT_CUTOFF = 5.days
        const val NEW_SERVICE_FEATURE_FLAG = "new_service_home_screen"
    }

    operator fun invoke(): Flow<List<ServiceAlert>> {
        return flow {
            val now = clock.now()
            if (!remoteConfigRepository.feature(NEW_SERVICE_FEATURE_FLAG)) {
                emit(listOf())
                return@flow
            }

            emit(
                serviceAlertRepository.alerts().item.filter {
                    it.date?.let { now - it < NEW_ALERT_CUTOFF } ?: false
                }
            )
        }
    }

}