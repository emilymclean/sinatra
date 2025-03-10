package cl.emilym.sinatra.data.models

import cl.emilym.gtfs.JourneySearchConfigEndpoint
import cl.emilym.sinatra.router.RaptorConfig
import kotlin.time.Duration
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
    val transferTime: Duration,
    val transferPenalty: Int,
    val changeOverTime: Duration,
    val changeOverPenalty: Int
) {

    val raptor: RaptorConfig get() = RaptorConfig(
        maximumWalkingTime.inWholeSeconds,
        transferTime.inWholeSeconds,
        transferPenalty,
        changeOverTime.inWholeSeconds,
        changeOverPenalty,
    )

    companion object {
        fun fromPb(pb: cl.emilym.gtfs.JourneySearchOption): JourneySearchOption {
            val transferTime = pb.transferTime?.milliseconds ?: ZERO
            val changeOverTime = pb.changeOverTime?.milliseconds ?: ZERO
            return JourneySearchOption(
                pb.maximumWalkingTime?.milliseconds ?: ZERO,
                transferTime,
                pb.transferPenalty ?: transferTime.inWholeSeconds.toInt(),
                changeOverTime,
                pb.changeOverPenalty ?: changeOverTime.inWholeSeconds.toInt(),
            )
        }
    }

}