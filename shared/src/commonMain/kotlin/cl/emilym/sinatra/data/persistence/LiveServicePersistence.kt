package cl.emilym.sinatra.data.persistence

import com.google.transit.realtime.FeedMessage
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single
class LiveServicePersistence {

    val tracked = mutableMapOf<String, Flow<FeedMessage>>()

}