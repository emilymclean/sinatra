package cl.emilym.sinatra.router

import cl.emilym.sinatra.RouterException
import cl.emilym.sinatra.data.models.ServiceId
import cl.emilym.sinatra.data.models.StopId
import cl.emilym.sinatra.nullIfEmpty
import cl.emilym.sinatra.router.data.EdgeType
import cl.emilym.sinatra.router.data.NetworkGraph
import cl.emilym.sinatra.router.data.NetworkGraphEdge

data class RaptorStop(
    val id: StopId,
    val addedTime: Seconds
)

data class RaptorConfig(
    val maximumWalkingTime: Seconds,
    val transferTime: Seconds,
    val transferPenalty: Int,
    val changeOverTime: Seconds,
    val changeOverPenalty: Int,
)

class Raptor(
    override val graph: NetworkGraph,
    override val activeServiceIds: List<List<ServiceId>>,
    override val config: RaptorConfig
): BaseRouter() {

    fun calculate(
        departureTime: DaySeconds,
        departureStop: StopId,
        arrivalStop: StopId
    ): RaptorJourney {
        return calculate(
            departureTime,
            listOf(RaptorStop(departureStop, 0L)),
            listOf(RaptorStop(arrivalStop, 0L))
        )
    }

    fun calculate(
        departureTime: DaySeconds,
        departureStops: List<RaptorStop>,
        arrivalStops: List<RaptorStop>
    ): RaptorJourney {
        return doCalculation(
            departureTime,
            departureStops,
            arrivalStops
        )
    }

    override fun initializeDjikstra(
        Q: FibonacciHeap<Int, Long>,
        dist: Array<Long>,
        distP: Array<Long>,
        departureStopIndices: List<Pair<Int, RaptorStop>>,
        arrivalStopIndices: List<Pair<Int, RaptorStop>>
    ) {
        for (departureStopIndex in departureStopIndices) {
            dist[departureStopIndex.first] = departureStopIndex.second.addedTime
            distP[departureStopIndex.first] = departureStopIndex.second.addedTime
            Q.add(departureStopIndex.first, 0)
        }
    }

    override fun isValidTravelEdge(
        edge: NetworkGraphEdge,
        dayAdjustment: Seconds,
        anchorTime: DaySeconds
    ): Boolean = (edge.departureTime.toLong() + dayAdjustment + 60) < anchorTime

    override fun getTravelAnchorTime(edge: NetworkGraphEdge, dayAdjustment: Seconds): Long =
        (edge.departureTime.toLong() + dayAdjustment)

}