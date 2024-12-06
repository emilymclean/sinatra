package cl.emilym.sinatra.domain.search

import cl.emilym.sinatra.data.models.Stop
import org.koin.core.annotation.Factory

@Factory
class StopTypeSearcher: TypeSearcher<Stop>() {

    override fun fields(t: Stop) = listOf(t.id, t.name)
}