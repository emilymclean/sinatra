package cl.emilym.sinatra.ui.maps

import androidx.compose.ui.geometry.Size
import cl.emilym.sinatra.data.models.MapLocation
import cl.emilym.sinatra.data.models.MapRegion
import cl.emilym.sinatra.data.models.ScreenLocation
import cl.emilym.sinatra.data.models.ScreenRegion
import cl.emilym.sinatra.ui.addCoordinateSpan
import cl.emilym.sinatra.ui.toCoordinateSpan
import io.github.aakira.napier.Napier

interface MapControl {
    fun zoomToArea(bounds: MapRegion, padding: Int)
    fun zoomToArea(topLeft: MapLocation, bottomRight: MapLocation, padding: Int)
    fun zoomToPoint(location: MapLocation, zoom: Float = 16f)
}

abstract class AbstractMapControl: MapControl {
    protected abstract val contentViewportPadding: PrecomputedPaddingValues
    protected abstract val contentViewportSize: Size
    protected abstract val bottomSheetHalfHeight: Float

    private val visibleMapSize: Size get() =
        Size(contentViewportSize.width, contentViewportSize.height * (1 - bottomSheetHalfHeight))
    private val contentViewportAspect: Float get() = contentViewportSize.width / contentViewportSize.height
    private val visibleMapAspect: Float get() = visibleMapSize.width / visibleMapSize.height

    private fun boxOverOther(box: ScreenRegion, aspect: Float, padding: PrecomputedPaddingValues): ScreenRegion {
        val boxAspect = box.aspect
        val width = if (boxAspect > aspect) box.width.toFloat() else (box.height * aspect)
        val height = if (boxAspect <= aspect) box.height.toFloat() else (box.width / aspect)

        val padding = padding * (width / contentViewportSize.width)

        return ScreenRegion(
            topLeft = ScreenLocation(
                (box.topLeft.x - (width / 2) + (box.width / 2)).toInt() - padding.left,
                (box.topLeft.y - (height / 2) + (box.height / 2)).toInt() - (padding.top / 2)
            ),
            bottomRight = ScreenLocation(
                (box.topLeft.x + (width / 2) + (box.width / 2)).toInt() + padding.right,
                (box.topLeft.y + (height / 2) + (box.height / 2)).toInt()
            )
        ).order()
    }

    abstract fun toScreenSpace(location: MapLocation): ScreenLocation?
    abstract fun toMapSpace(coordinate: ScreenLocation): MapLocation?

    abstract fun showBounds(bounds: MapRegion)
    abstract fun showPoint(center: MapLocation, zoom: Float)

    override fun zoomToArea(
        topLeft: MapLocation,
        bottomRight: MapLocation,
        padding: Int
    ) {
        val screenSpace = listOfNotNull(toScreenSpace(topLeft), toScreenSpace(bottomRight))
        if (screenSpace.size != 2) return showBounds(
            MapRegion(topLeft, bottomRight)
        )

        val viewportBox = boxOverOther(
            ScreenRegion(
                screenSpace[0],
                screenSpace[1],
            ).order(),
            visibleMapAspect,
            PrecomputedPaddingValues.all(padding)
        )

        val screenBox = viewportBox
            .copy(
                bottomRight = ScreenLocation(
                    viewportBox.bottomRight.x,
                    (viewportBox.topLeft.y + (viewportBox.width / contentViewportAspect)).toInt(),
                )
            )
            .order()


        Napier.d("Screen box = $screenBox, aspect = ${screenBox.width.toFloat() / screenBox.height.toFloat()}, target = ${contentViewportAspect}")

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

    override fun zoomToArea(bounds: MapRegion, padding: Int) {
        zoomToArea(bounds.topLeft, bounds.bottomRight, padding)
    }

    override fun zoomToPoint(
        location: MapLocation,
        zoom: Float
    ) {
        val span = zoom.toCoordinateSpan(
            contentViewportSize
        )
        val region = location.addCoordinateSpan(span)
        Napier.d("Region centerpoint = ${region.center}")
        zoomToArea(
            region,
            0
        )
    }

}