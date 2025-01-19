package cl.emilym.sinatra.data.models

import cl.emilym.sinatra.router.Milliseconds
import cl.emilym.sinatra.router.RaptorConfig
import cl.emilym.sinatra.router.Seconds
import kotlin.time.Duration

data class JourneySearchConfig(
    val maximumComputationTime: Duration,
    val options: List<JourneySearchOption>
)

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

}