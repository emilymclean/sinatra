package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.room.dao.ShaDao
import org.koin.core.annotation.Factory

@Factory
class ShaRepository(
    private val shaDao: ShaDao
) {
}