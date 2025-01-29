package cl.emilym.sinatra.data.models

import cl.emilym.gtfs.JourneySearchConfigEndpoint
import cl.emilym.sinatra.router.RaptorConfig
import kotlin.time.Duration
import kotlin.time.Duration.Companion.INFINITE
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds

data class JourneySearchConfig(
    val maximumComputationTime: Duration,
    val options: List<JourneySearchOption>
) {

    companion object {
        fun fromPb(pb: JourneySearchConfigEndpoint): JourneySearchConfig {
            return JourneySearchConfig(
                pb.maximumComputationTime.milliseconds,
                pb.options.map { JourneySearchOption.fromPb(it) }
            )
        }
    }

}

data class JourneySearchOption(
    val maximumWalkingTime: Duration,
    val transferPenalty: Duration,
    val changeOverPenalty: Duration
) {

    val raptor: RaptorConfig get() = RaptorConfig(
        maximumWalkingTime.inWholeSeconds,
        transferPenalty.inWholeSeconds,
        changeOverPenalty.inWholeSeconds
    )

    companion object {
        fun fromPb(pb: cl.emilym.gtfs.JourneySearchOption): JourneySearchOption {
            return JourneySearchOption(
                pb.maximumWalkingTime?.milliseconds ?: ZERO,
                pb.transferPenalty?.milliseconds ?: ZERO,
                pb.changeOverPenalty?.milliseconds ?: ZERO
            )
        }
    }

}