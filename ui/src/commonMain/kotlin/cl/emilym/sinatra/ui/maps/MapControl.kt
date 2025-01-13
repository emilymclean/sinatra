package cl.emilym.sinatra.ui.maps

import androidx.compose.runtime.Composable
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

@Composable
expect fun rememberMapControl(): MapControl

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

class SafeMapControl: MapControl {
    var wrapped: MapControl? = null

    override fun zoomToArea(bounds: MapRegion, padding: Dp) {
        wrapped?.zoomToArea(bounds, padding)
    }

    override fun zoomToArea(topLeft: MapLocation, bottomRight: MapLocation, padding: Dp) {
        wrapped?.zoomToArea(topLeft, bottomRight, padding)
    }

    override fun zoomToPoint(location: MapLocation, zoom: Zoom) {
        wrapped?.zoomToPoint(location, zoom)
    }

    override fun moveToPoint(location: MapLocation, minZoom: Zoom?) {
        wrapped?.moveToPoint(location, minZoom)
    }

    override val zoom: Float
        get() = wrapped?.zoom ?: 0f
}

abstract class AbstractMapControl: MapControl, MapProjectionProvider {
    protected abstract val contentViewportPadding: PrecomputedPaddingValues
    protected abstract val contentViewportSize: Size
    protected abstract val density: Density
    protected abstract val bottomSheetHalfHeight: Float

    private val paddedContentViewportSize: Size get() = Size(
        (contentViewportSize.width - contentViewportPadding.horizontal),
        (contentViewportSize.height - contentViewportPadding.vertical)
    )
    private val visibleMapSize: Size get() =
        Size(
            paddedContentViewportSize.width,
            (contentViewportSize.height * (1 - bottomSheetHalfHeight)) - contentViewportPadding.vertical
        )

    private val contentViewportAspect: Float get() = contentViewportSize.width / contentViewportSize.height
    private val paddedContentViewportAspect: Float get() =
        paddedContentViewportSize.width / paddedContentViewportSize.height
    private val visibleMapAspect: Float get() = visibleMapSize.width / visibleMapSize.height

    private fun boxOverOther(box: ScreenRegion, aspect: Float): ScreenRegion {
        val boxAspect = box.aspect

        val width = if (boxAspect > aspect) box.width else (box.height * aspect)
        val height = if (boxAspect > aspect) (box.width / aspect) else box.height

        val halfWidth = width / 2
        val halfHeight = height / 2


        val centre = box.centre

        return ScreenRegion(
            topLeft = ScreenLocation(
                centre.x - halfWidth,
                centre.y - halfHeight
            ),
            bottomRight = ScreenLocation(
                centre.x + halfWidth,
                centre.y + halfHeight
            )
        )
    }

    abstract fun showBounds(bounds: MapRegion)

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
            )
            .run {
               padded(PrecomputedPaddingValues.all(padding.value * density.density * (width / visibleMapSize.width)))
            },
            visibleMapAspect
        )

        val screenBox = viewportBox
            .copy(
                bottomRight = ScreenLocation(
                    viewportBox.bottomRight.x,
                    viewportBox.topLeft.y + (viewportBox.width / paddedContentViewportAspect),
                )
            )


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
        zoomToArea(
            region,
            0.dp
        )
    }

    override fun moveToPoint(location: MapLocation, minZoom: Float?) {
        zoomToPoint(
            location,
            when (minZoom) {
                null -> zoom
                else -> max(zoom, minZoom)
            }
        )
    }

}