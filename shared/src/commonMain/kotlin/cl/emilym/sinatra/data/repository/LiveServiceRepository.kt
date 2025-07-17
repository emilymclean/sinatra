package cl.emilym.sinatra.data.repository

import cl.emilym.sinatra.FeatureFlag
import cl.emilym.sinatra.data.client.LiveServiceClient
import cl.emilym.sinatra.data.models.DelayInformation
import cl.emilym.sinatra.data.models.RealtimeInformationImpl
import cl.emilym.sinatra.data.models.RealtimeUpdateImpl
import cl.emilym.sinatra.data.models.RouteId
import cl.emilym.sinatra.data.models.RouteRealtimeInformation
import cl.emilym.sinatra.data.models.StopDelayInformation
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.data.models.StopRealtimeInformation
import cl.emilym.sinatra.lib.TTLMemoryCache
import cl.emilym.sinatra.lib.periodicFlow
import com.google.transit.realtime.FeedMessage
import com.google.transit.realtime.TripUpdate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Single
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

interface ILiveServiceRepository {
    fun getRouteRealtimeUpdates(routeId: RouteId): Flow<RouteRealtimeInformation>
    fun getStopRealtimeUpdates(stopId: StopId): Flow<StopRealtimeInformation>
}

@Single
class FeedMessageLiveServiceRepository(
    private val liveServiceClient: LiveServiceClient,
    private val clock: Clock
): ILiveServiceRepository {
    private val feedMessageCache = TTLMemoryCache<FeedMessage> {
        liveServiceClient.tripUpdates()
    }

    private fun parseStopTimeUpdate(update: TripUpdate.StopTimeUpdate?): Duration? {
        update ?: return null
        return parseStopTimeEvent(update.arrival) ?: parseStopTimeEvent(update.departure)
    }

    private fun parseStopTimeEvent(event: TripUpdate.StopTimeEvent?): Duration? {
        return when {
            event == null -> null
            event.time != null -> Instant.fromEpochMilliseconds(
                event.time
            ) - clock.now()
            event.delay != null -> event.delay.seconds
            else -> null
        }
    }

    private fun shared(stopId: StopId?): Flow<RealtimeInformationImpl> {
        return periodicFlow().mapLatest {
            val update = feedMessageCache.get()

            RealtimeInformationImpl(
                update.entity.mapNotNull {
                    if (it.tripUpdate == null) return@mapNotNull null
                    val specific = it.tripUpdate.stopTimeUpdate.firstOrNull { it.stopId == stopId }
                    var delay: Duration? = null
                    if (specific != null && stopId != null) {
                        delay = parseStopTimeUpdate(specific)
                    }
                    if (delay == null) {
                        delay = when {
                            it.tripUpdate.timestamp != null -> Instant.fromEpochMilliseconds(
                                it.tripUpdate.timestamp
                            ) - clock.now()
                            it.tripUpdate.delay != null -> it.tripUpdate.delay.seconds
                            else -> null
                        }
                    }

                    RealtimeUpdateImpl(
                        it.tripUpdate.trip.tripId ?: return@mapNotNull null,
                        when(delay) {
                            null -> DelayInformation.Unknown
                            else -> DelayInformation.Fixed(delay)
                        },
                        it.tripUpdate.stopTimeUpdate.mapNotNull {
                            StopDelayInformation(
                                it.stopId ?: return@mapNotNull null,
                                DelayInformation.Fixed(
                                    parseStopTimeUpdate(it) ?: return@mapNotNull null
                                )
                            )
                        }
                    )
                },
                when (update.header.timestamp) {
                    null, 0L -> clock.now()
                    else -> Instant.fromEpochMilliseconds(update.header.timestamp)
                } + 2.minutes
            )
        }
    }

    override fun getRouteRealtimeUpdates(routeId: RouteId): Flow<RouteRealtimeInformation> {
        return shared(null)
    }

    override fun getStopRealtimeUpdates(stopId: StopId): Flow<StopRealtimeInformation> {
        return shared(null)
    }
}

@Factory
class DomainLiveServiceRepository(
    private val liveServiceClient: LiveServiceClient
): ILiveServiceRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getRouteRealtimeUpdates(routeId: RouteId): Flow<RouteRealtimeInformation> {
        return periodicFlow().mapLatest {
            liveServiceClient.getRouteRealtime(routeId)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getStopRealtimeUpdates(stopId: StopId): Flow<StopRealtimeInformation> {
        return periodicFlow().mapLatest {
            liveServiceClient.getStopRealtime(stopId)
        }
    }
}

@Factory
class LiveServiceRepository(
    private val domainLiveServiceRepository: DomainLiveServiceRepository,
    private val feedMessageLiveServiceRepository: FeedMessageLiveServiceRepository,
    private val remoteConfigRepository: RemoteConfigRepository
): ILiveServiceRepository {

    private suspend fun useFeedMessage(): Boolean = remoteConfigRepository.feature(
        FeatureFlag.REALTIME_USE_GLOBAL
    )

    override fun getRouteRealtimeUpdates(routeId: RouteId) = flow {
        emitAll(when (useFeedMessage()) {
            true -> feedMessageLiveServiceRepository.getRouteRealtimeUpdates(routeId)
            else -> domainLiveServiceRepository.getRouteRealtimeUpdates(routeId)
        })
    }

    override fun getStopRealtimeUpdates(stopId: StopId) = flow {
        emitAll(when (useFeedMessage()) {
            true -> feedMessageLiveServiceRepository.getStopRealtimeUpdates(stopId)
            else -> domainLiveServiceRepository.getStopRealtimeUpdates(stopId)
        })
    }
}