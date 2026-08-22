package cl.emilym.sinatra.domain

import cl.emilym.sinatra.data.models.MapRegion
import cl.emilym.sinatra.data.models.distance
import org.koin.core.annotation.Factory
import kotlin.math.abs

@Factory
class RegionDistinctUseCase() {

    operator fun invoke(old: MapRegion, new: MapRegion): Boolean {
        val centerDistance = distance(old.center, new.center)

        // Use the larger of the two diagonals as the reference scale for movement,
        // so a shrinking region doesn't make movement checks artificially strict.
        val referenceDiagonal = maxOf(old.diagonalDistance, new.diagonalDistance)
        val moveThreshold = maxOf(
            referenceDiagonal * MOVE_THRESHOLD_RATIO,
            MIN_ABSOLUTE_MOVE_DISTANCE
        )

        val hasMoved = centerDistance > moveThreshold

        // Only bother checking resize if at least one region is "big enough"
        // to care about; below that, size differences are noise.
        val bothSmall = old.diagonalDistance < MIN_DIAGONAL_SIZE_CHECK &&
                new.diagonalDistance < MIN_DIAGONAL_SIZE_CHECK

        val hasResized = if (bothSmall) {
            false
        } else {
            val relativeSizeChange = abs(new.diagonalDistance - old.diagonalDistance) /
                    maxOf(old.diagonalDistance, 1e-9) // guard div-by-zero
            relativeSizeChange > RESIZE_THRESHOLD_RATIO
        }

        return hasMoved || hasResized
    }

    companion object {
        const val MOVE_THRESHOLD_RATIO: Double = 0.15
        const val RESIZE_THRESHOLD_RATIO: Double = 0.20
        const val MIN_DIAGONAL_SIZE_CHECK: Double = 50.0
        const val MIN_ABSOLUTE_MOVE_DISTANCE: Double = 5.0
    }

}