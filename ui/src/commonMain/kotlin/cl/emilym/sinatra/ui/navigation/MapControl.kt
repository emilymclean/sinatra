package cl.emilym.sinatra.ui.navigation

import androidx.compose.ui.geometry.Size
import cl.emilym.sinatra.data.models.MapRegion
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.ScreenLocation
import cl.emilym.sinatra.data.models.ScreenRegion
import kotlin.math.PI
import kotlin.math.pow

interface MapControl {
    fun zoomToArea(bounds: MapRegion, padding: Int)
    fun zoomToArea(topLeft: MapLocation, bottomRight: MapLocation, padding: Int)
    fun zoomToPoint(location: MapLocation, zoom: Float = 16f)
}

abstract class AbstractMapControl: MapControl {
    protected abstract val contentViewportSize: Size
    protected abstract val bottomSheetHalfHeight: Float

    private val visibleMapSize: Size get() =
        Size(contentViewportSize.width, contentViewportSize.height * (1 - bottomSheetHalfHeight))
    private val contentViewportAspect: Float get() = contentViewportSize.width / contentViewportSize.height
    private val visibleMapAspect: Float get() = visibleMapSize.width / visibleMapSize.height

    private fun boxOverOther(box: ScreenRegion, aspect: Float): ScreenRegion {
        val boxAspect = box.aspect
        val width = if (boxAspect > aspect) box.width.toFloat() else (box.height * aspect)
        val height = if (boxAspect <= aspect) box.height.toFloat() else (box.width / aspect)

        return ScreenRegion(
            topLeft = ScreenLocation(
                (box.topLeft.x - (width / 2) + (box.width / 2)).toInt(),
                (box.topLeft.y - (height / 2) + (box.height / 2)).toInt()
            ),
            bottomRight = ScreenLocation(
                (box.topLeft.x + (width / 2) + (box.width / 2)).toInt(),
                (box.topLeft.y + (height / 2) + (box.height / 2)).toInt()
            )
        )
    }

    abstract fun toScreenSpace(location: MapLocation): ScreenLocation
    abstract fun toMapSpace(coordinate: ScreenLocation): MapLocation

    abstract fun showBounds(bounds: MapRegion)
    abstract fun showPoint(center: MapLocation, zoom: Float)

    override fun zoomToArea(
        topLeft: MapLocation,
        bottomRight: MapLocation,
        padding: Int
    ) {
        val viewportBox = boxOverOther(
            ScreenRegion(
                toScreenSpace(topLeft),
                toScreenSpace(bottomRight),
            ),
            visibleMapAspect
        )

        val screenBox = viewportBox.copy(
            bottomRight = ScreenLocation(
                viewportBox.bottomRight.x,
                (viewportBox.bottomRight.y + (viewportBox.width / contentViewportAspect)).toInt(),
            )
        ).padded(padding)

        val bounds = MapRegion(
            toMapSpace(screenBox.topLeft),
            toMapSpace(screenBox.bottomRight),
        )
        showBounds(bounds)
    }

    override fun zoomToArea(bounds: MapRegion, padding: Int) {
        zoomToArea(bounds.topLeft, bounds.bottomRight, padding)
    }

    override fun zoomToPoint(
        location: MapLocation,
        zoom: Float
    ) {
        val moveDownPx = ((contentViewportSize.height / 2) - (visibleMapSize.height / 2)) * -1.75f
        showPoint(location.addMetersLatitude(metersPerPxAtZoom(zoom) * moveDownPx), zoom)
    }


}

const val EARTH_CIRCUMFERENCE = 6378137

fun metersPerPxAtZoom(zoom: Float): Float {
    return EARTH_CIRCUMFERENCE / (256 * 2f.pow(zoom))
}

fun MapLocation.addMetersLatitude(meters: Float): MapLocation {
    return MapLocation(
        lat + (meters / EARTH_CIRCUMFERENCE) * (180 / PI),
        lng
    )
}