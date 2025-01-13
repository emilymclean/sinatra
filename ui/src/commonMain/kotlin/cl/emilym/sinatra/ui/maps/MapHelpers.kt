package cl.emilym.sinatra.ui.maps

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import cl.emilym.sinatra.asDegrees
import cl.emilym.sinatra.asRadians
import cl.emilym.sinatra.data.models.CoordinateSpan
import cl.emilym.sinatra.data.models.Degree
import cl.emilym.sinatra.data.models.DensityPixel
import cl.emilym.sinatra.data.models.Latitude
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.MapRegion
import cl.emilym.sinatra.data.models.Radian
import cl.emilym.sinatra.data.models.ScreenLocation
import cl.emilym.sinatra.data.models.ScreenRegionSizeDp
import cl.emilym.sinatra.data.models.ScreenRegionSizePx
import cl.emilym.sinatra.data.models.Zoom
import cl.emilym.sinatra.degrees
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

@Deprecated("Don't done work :(")
fun Zoom.toCoordinateSpan(
    viewportSize: ScreenRegionSizePx
): CoordinateSpan {
    val span = 360 / 2.0.pow(this.toDouble())
    return CoordinateSpan(
        deltaLatitude = ((viewportSize.width) / 256.0) * span,
        deltaLongitude = ((viewportSize.width) / 256.0) * span
    )
}

fun CoordinateSpan.adjustForLatitude(latitude: Latitude): CoordinateSpan {
    return CoordinateSpan(
        deltaLatitude,
        deltaLongitude * cos(latitude.degrees.asRadians)
    )
}

fun MapRegion.toZoom(mapWidth: DensityPixel, mapHeight: DensityPixel): Float {
    val worldSize = 256.0

    fun latRad(lat: Degree): Radian {
        val s = sin(lat.asRadians)
        val radX2 = ln((1 + s) / (1 - s)) / 2.0
        return (max(min(radX2, PI),-PI) / 2.0)
    }

    fun zoom(mapPx: Float, mapFrac: Double): Double {
        return ln(mapPx / worldSize / mapFrac) / ln(2.0)
    }

    val ne = northEast
    val sw = southWest

    val latF = abs(latRad(ne.lat) - latRad(sw.lat)) / PI

    val lngD = ne.lng - sw.lng
    val lngF = (if (lngD < 0) lngD + 360 else lngD) / 360.0

    val latZ = zoom(mapHeight, latF)
    val lngZ = zoom(mapWidth, lngF)

    return min(lngZ, latZ).toFloat().coerceAtLeast(0f)
}

fun MapRegion.toZoom(
    visibleMapSize: ScreenRegionSizeDp
): Zoom {
    return toZoom(
        visibleMapSize.width,
        visibleMapSize.height
    )
}

fun MapRegion.toZoom(
    visibleMapSize: ScreenRegionSizePx,
    density: Density
): Zoom {
    return toZoom(visibleMapSize.dp(density.density))
}

fun calculateVisibleMapSize(
    bottomSheetHalfHeight: Float,
    contentViewportSize: ScreenRegionSizePx,
    contentViewportPadding: PrecomputedPaddingValues
): ScreenRegionSizePx {
    return ScreenRegionSizePx(
        (contentViewportSize.width - contentViewportPadding.horizontal),
        (contentViewportSize.height * (1 - bottomSheetHalfHeight)) - contentViewportPadding.vertical
    )
}

fun MapProjectionProvider.calculateZoom(
    nativeZoom: Float,
    visibleMapSize: ScreenRegionSizePx,
    density: Density
): Zoom {
    val screen = listOf(
        ScreenLocation(0f,0f),
        ScreenLocation(
            visibleMapSize.width,
            visibleMapSize.height
        )
    )
    val map = screen.mapNotNull { toMapSpace(it) }
    if (map.size != 2) return nativeZoom
    return MapRegion(map[0], map[1]).toZoom(visibleMapSize, density)
}

private data class PixelSpace(
    val x: Double,
    val y: Double
)

// https://github.com/goto10/EBCExtensions
const val MERCATOR_RADIUS = 85445659.44705395
const val MERCATOR_OFFSET = 268435456

private fun MapLocation.toPixelSpace(): PixelSpace {
    val s = sin(lat.asRadians)

    return PixelSpace(
        (MERCATOR_OFFSET + MERCATOR_RADIUS * lng.asRadians),
        (MERCATOR_OFFSET - MERCATOR_RADIUS * (ln((1 + s) / (1 - s)) / 2))
    )
}

private fun PixelSpace.toMapSpace(): MapLocation {
    return MapLocation(
        (PI / 2.0 - 2.0 * atan(exp((y - MERCATOR_OFFSET) / MERCATOR_RADIUS))).asDegrees,
        ((x - MERCATOR_OFFSET) / MERCATOR_RADIUS).asDegrees
    )
}

fun Zoom.toMapRegion(centre: MapLocation, mapWidth: DensityPixel, mapHeight: DensityPixel): MapRegion {
    val zoom = this
    val centrePixelSpace = centre.toPixelSpace()
    val zoomScale = 2f.pow(21 - zoom).toDouble()

    val scaledMapSize = PixelSpace(mapWidth * zoomScale, mapHeight * zoomScale)
    val topLeftPixel = PixelSpace(
        centrePixelSpace.x - (scaledMapSize.x / 2f),
        centrePixelSpace.y - (scaledMapSize.y / 2f),
    )
    val bottomRightPixel = PixelSpace(
        centrePixelSpace.x + (scaledMapSize.x / 2f),
        centrePixelSpace.y + (scaledMapSize.y / 2f),
    )

    return MapRegion(
        topLeftPixel.toMapSpace(),
        bottomRightPixel.toMapSpace()
    )
}