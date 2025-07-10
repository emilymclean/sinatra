package cl.emilym.sinatra.data.models

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

interface RealtimeInformation<T: RealtimeUpdate> {
    val updates: List<T>
    val expire: Instant
}

interface RealtimeUpdate {
    val delay: DelayInformation
}

sealed interface DelayInformation {
    data object Unknown: DelayInformation
    data class Fixed(
        val delay: Duration
    ): DelayInformation
}

data class RouteRealtimeInformation(
    override val updates: List<RouteRealtimeUpdate>,
    override val expire: Instant
): RealtimeInformation<RouteRealtimeUpdate> {

    companion object {
        fun fromPb(pb: cl.emilym.gtfs.RealtimeEndpoint): RouteRealtimeInformation {
            return RouteRealtimeInformation(
                pb.updates.map { RouteRealtimeUpdate.fromPb(it) },
                pb.expireTimestamp?.let { Instant.parse(pb.expireTimestamp) }
                    ?: (kotlin.time.Clock.System.now() + 2.minutes)
            )
        }
    }

}

data class RouteRealtimeUpdate(
    val tripId: TripId,
    override val delay: DelayInformation
): RealtimeUpdate {

    companion object {
        fun fromPb(pb: cl.emilym.gtfs.RealtimeUpdate): RouteRealtimeUpdate {
            return RouteRealtimeUpdate(
                pb.tripId,
                pb.delay.let {
                    when (it) {
                        null -> DelayInformation.Unknown
                        else -> DelayInformation.Fixed(it.seconds)
                    }
                }
            )
        }
    }

}

data class StopRealtimeInformation(
    override val updates: List<StopRealtimeUpdate>,
    override val expire: Instant
): RealtimeInformation<StopRealtimeUpdate> {

    companion object {
        fun fromPb(pb: cl.emilym.gtfs.RealtimeEndpoint): StopRealtimeInformation {
            return StopRealtimeInformation(
                pb.updates.map { StopRealtimeUpdate.fromPb(it) },
                pb.expireTimestamp?.let { Instant.parse(pb.expireTimestamp) }
                    ?: (kotlin.time.Clock.System.now() + 2.minutes)
            )
        }
    }

}

data class StopRealtimeUpdate(
    val tripId: TripId,
    override val delay: DelayInformation
): RealtimeUpdate {

    companion object {
        fun fromPb(pb: cl.emilym.gtfs.RealtimeUpdate): StopRealtimeUpdate {
            return StopRealtimeUpdate(
                pb.tripId,
                pb.delay.let {
                    when (it) {
                        null -> DelayInformation.Unknown
                        else -> DelayInformation.Fixed(it.seconds)
                    }
                }
            )
        }
    }

}