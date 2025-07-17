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

typealias RouteRealtimeInformation = RealtimeInformationImpl
typealias RouteRealtimeUpdate = RealtimeUpdateImpl
typealias StopRealtimeInformation = RealtimeInformationImpl
typealias StopRealtimeUpdate = RealtimeUpdateImpl

data class RealtimeInformationImpl(
    override val updates: List<RealtimeUpdateImpl>,
    override val expire: Instant
): RealtimeInformation<RealtimeUpdateImpl> {

    companion object {
        fun fromPb(pb: cl.emilym.gtfs.RealtimeEndpoint): RealtimeInformationImpl {
            return RealtimeInformationImpl(
                pb.updates.map { RealtimeUpdateImpl.fromPb(it) },
                pb.expireTimestamp?.let { Instant.parse(pb.expireTimestamp) }
                    ?: (Clock.System.now() + 2.minutes)
            )
        }
    }

}

data class RealtimeUpdateImpl(
    val tripId: TripId,
    override val delay: DelayInformation,
    val stopDelayInformation: List<StopDelayInformation>
): RealtimeUpdate {

    companion object {
        fun fromPb(pb: cl.emilym.gtfs.RealtimeUpdate): RealtimeUpdateImpl {
            return RealtimeUpdateImpl(
                pb.tripId,
                pb.delay.let {
                    when (it) {
                        null -> DelayInformation.Unknown
                        else -> DelayInformation.Fixed(it.seconds)
                    }
                },
                emptyList()
            )
        }
    }

}

data class StopDelayInformation(
    val stopId: StopId,
    val delay: DelayInformation
)