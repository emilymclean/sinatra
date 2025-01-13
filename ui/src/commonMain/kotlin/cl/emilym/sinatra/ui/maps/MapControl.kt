package cl.emilym.sinatra.ui.maps

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.MapRegion
import cl.emilym.sinatra.data.models.ScreenLocation
import cl.emilym.sinatra.data.models.ScreenRegion
import cl.emilym.sinatra.data.models.Zoom
import io.github.aakira.napier.Napier
import kotlin.math.max

interface MapProjectionProvider {
    fun toScreenSpace(location: MapLocation): ScreenLocation?
    fun toMapSpace(coordinate: ScreenLocation): MapLocation?
}

interface MapControl {
    fun zoomToArea(bounds: MapRegion, padding: Dp)
    fun zoomToArea(topLeft: MapLocation, bottomRight: MapLocation, padding: Dp)
    fun zoomToPoint(location: MapLocation, zoom: Zoom = 16f)
    fun moveToPoint(location: MapLocation, minZoom: Zoom? = null)

    val zoom: Float
}

abstract class AbstractMapControl: MapControl, MapProjectionProvider {
    protected abstract val contentViewportPadding: PrecomputedPaddingValues
    protected abstract val contentViewportSize: Size
    protected abstract val density: Density
    protected abstract val bottomSheetHalfHeight: Float

    private val visibleMapSize: Size get() = contentViewportSize
//        Size(contentViewportSize.width, contentViewportSize.height * (1 - bottomSheetHalfHeight))

    private val contentViewportAspect: Float get() = contentViewportSize.width / contentViewportSize.height
    private val visibleMapAspect: Float get() = visibleMapSize.width / visibleMapSize.height

    private fun boxOverOther(box: ScreenRegion, aspect: Float): ScreenRegion {
        val boxAspect = box.aspect
        val width = if (boxAspect > aspect) box.width else (box.height * aspect)
        val height = if (boxAspect <= aspect) box.height else (box.width / aspect)

        return ScreenRegion(
            topLeft = ScreenLocation(
                (box.topLeft.x - (width / 2) + (box.width / 2)),
                (box.topLeft.y - (height / 2) + (box.height / 2))
            ),
            bottomRight = ScreenLocation(
                (box.topLeft.x + (width / 2) + (box.width / 2)),
                (box.topLeft.y + (height / 2) + (box.height / 2))
            )
        )
    }

    abstract fun showBounds(bounds: MapRegion)
    abstract fun showPoint(center: MapLocation, zoom: Zoom)

    abstract val nativeZoom: Float
    override val zoom: Float
        get() = calculateZoom(nativeZoom, visibleMapSize, density)


    override fun zoomToArea(
        topLeft: MapLocation,
        bottomRight: MapLocation,
        padding: Dp
    ) {
        val screenSpace = listOfNotNull(toScreenSpace(topLeft), toScreenSpace(bottomRight))
        if (screenSpace.size != 2) return showBounds(
            MapRegion(topLeft, bottomRight)
        )

        val viewportBox = boxOverOther(
            ScreenRegion(
                screenSpace[0],
                screenSpace[1],
            ),
            visibleMapAspect
        )

        val screenBox = viewportBox
            .copy(
                bottomRight = ScreenLocation(
                    viewportBox.bottomRight.x,
                    (viewportBox.topLeft.y + (viewportBox.width / contentViewportAspect)),
                )
            ).run {
                padded(
                    ((-contentViewportPadding) + PrecomputedPaddingValues.all(padding.value * density.density))
                            * (width / contentViewportSize.width)
                )
            }


        Napier.d("Screen box = ${screenBox.width}, ${screenBox.height}, aspect = ${screenBox.width / screenBox.height}, target = ${contentViewportAspect}, padding = ${contentViewportPadding}")

        val mapSpace = listOfNotNull(toMapSpace(screenBox.topLeft), toMapSpace((screenBox.bottomRight)))
        if (mapSpace.size != 2) return showBounds(
            MapRegion(topLeft, bottomRight)
        )

        val bounds = MapRegion(
            mapSpace[0],
            mapSpace[1],
        ).order()
        showBounds(bounds)
    }

    override fun zoomToArea(bounds: MapRegion, padding: Dp) {
        zoomToArea(bounds.topLeft, bounds.bottomRight, padding)
    }

    override fun zoomToPoint(
        location: MapLocation,
        zoom: Float
    ) {
        val region = zoom.toMapRegion(
            location,
            visibleMapSize.width / density.density,
            visibleMapSize.height / density.density,
        )
        Napier.d("Region centerpoint = ${region.center}")
        zoomToArea(
            region,
            0.dp
        )
    }

    override fun moveToPoint(location: MapLocation, minZoom: Float?) {
        Napier.d(
            "Current zoom = ${zoom}, minZoom = ${minZoom}, (native = ${nativeZoom}, visibleMapSize = ${visibleMapSize}, contentViewportSize = ${contentViewportSize})",
            tag = "MapDebug"
        )
        zoomToPoint(
            location,
            when (minZoom) {
                null -> zoom
                else -> max(zoom, minZoom)
            }
        )
    }

}