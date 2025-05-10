package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.data.persistence.AppPersistence
import org.koin.core.annotation.Factory

@Factory
class AppRepository(
    private val appPersistence: AppPersistence
) {

    suspend fun lastAppCode() = appPersistence.lastAppCode()
    suspend fun setLastAppCode(value: Int) = appPersistence.setLastAppCode(value)

}