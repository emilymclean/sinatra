package cl.emilym.sinatra.ui.widgets.bottomsheet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue.Expanded
import androidx.compose.material3.SheetValue.PartiallyExpanded
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
fun SinatraBottomSheetScaffold(
    sheetContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    scaffoldState: SinatraBottomSheetScaffoldState = rememberSinatraBottomSheetScaffoldState(),
    sheetPeekHeight: Dp = 56.dp,
    sheetHalfHeight: Float = 0.5f,
    sheetMaxWidth: Dp = 640.dp,
    sheetContainerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    sheetContentColor: Color = contentColorFor(sheetContainerColor),
    sheetTonalElevation: Dp = 1.dp,
    sheetShadowElevation: Dp = 1.dp,
    sheetDragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    sheetSwipeEnabled: Boolean = true,
    topBar: @Composable (() -> Unit)? = null,
    snackbarHost: @Composable (SnackbarHostState) -> Unit = { SnackbarHost(it) },
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(containerColor),
    content: @Composable (PaddingValues) -> Unit
) {
    val peekHeightPx = with(LocalDensity.current) {
        sheetPeekHeight.roundToPx()
    }
    SinatraBottomSheetScaffoldLayout(
        modifier = modifier,
        topBar = topBar,
        body = content,
        snackbarHost = {
            snackbarHost(scaffoldState.snackbarHostState)
        },
        sheetPeekHeight = sheetPeekHeight,
        sheetOffset = { scaffoldState.bottomSheetState.requireOffset() },
        sheetState = scaffoldState.bottomSheetState,
        containerColor = containerColor,
        contentColor = contentColor,
        bottomSheet = { layoutHeight ->
            SinatraBottomSheet(
                state = scaffoldState.bottomSheetState,
                peekHeight = sheetPeekHeight,
                sheetMaxWidth = sheetMaxWidth,
                sheetHalfHeight = sheetHalfHeight,
                sheetSwipeEnabled = sheetSwipeEnabled,
                calculateAnchors = { sheetSize ->
                    val sheetHeight = sheetSize.height
                    sinatraDraggableAnchors {
                        if (!scaffoldState.bottomSheetState.skipPartiallyExpanded) {
                            SinatraSheetValue.PartiallyExpanded at (layoutHeight - peekHeightPx).toFloat()
                        }
                        if (sheetHeight != peekHeightPx) {
                            SinatraSheetValue.Expanded at maxOf(layoutHeight - sheetHeight, 0).toFloat()
                        }
                        if (!scaffoldState.bottomSheetState.skipHiddenState) {
                            SinatraSheetValue.Hidden at layoutHeight.toFloat()
                        }
                        SinatraSheetValue.HalfExpanded at maxOf(layoutHeight - (sheetHeight * sheetHalfHeight), 0f)
                    }
                },
                containerColor = sheetContainerColor,
                contentColor = sheetContentColor,
                tonalElevation = sheetTonalElevation,
                shadowElevation = sheetShadowElevation,
                dragHandle = sheetDragHandle,
                content = sheetContent
            )
        }
    )
}

@Stable
class SinatraBottomSheetScaffoldState(
    val bottomSheetState: SinatraSheetState,
    val snackbarHostState: SnackbarHostState
)

/**
 * Create and [remember] a [BottomSheetScaffoldState].
 *
 * @param bottomSheetState the state of the standard bottom sheet. See
 * [rememberStandardBottomSheetState]
 * @param snackbarHostState the [SnackbarHostState] used to show snackbars inside the scaffold
 */
@Composable
fun rememberSinatraBottomSheetScaffoldState(
    bottomSheetState: SinatraSheetState = rememberSinatraBottomSheetState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
): SinatraBottomSheetScaffoldState {
    return remember(bottomSheetState, snackbarHostState) {
        SinatraBottomSheetScaffoldState(
            bottomSheetState = bottomSheetState,
            snackbarHostState = snackbarHostState
        )
    }
}

/**
 * Create and [remember] a [SheetState] for [BottomSheetScaffold].
 *
 * @param initialValue the initial value of the state. Should be either [PartiallyExpanded] or
 * [Expanded] if [skipHiddenState] is true
 * @param confirmValueChange optional callback invoked to confirm or veto a pending state change
 * @param [skipHiddenState] whether Hidden state is skipped for [BottomSheetScaffold]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberSinatraBottomSheetState(
    initialValue: SinatraSheetValue = SinatraSheetValue.PartiallyExpanded,
    confirmValueChange: (SinatraSheetValue) -> Boolean = { true },
    skipHiddenState: Boolean = true,
) = rememberSinatraSheetState(false, confirmValueChange, initialValue, skipHiddenState)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SinatraBottomSheetScaffoldLayout(
    modifier: Modifier,
    topBar: @Composable (() -> Unit)?,
    body: @Composable (innerPadding: PaddingValues) -> Unit,
    bottomSheet: @Composable (layoutHeight: Int) -> Unit,
    snackbarHost: @Composable () -> Unit,
    sheetPeekHeight: Dp,
    sheetOffset: () -> Float,
    sheetState: SinatraSheetState,
    containerColor: Color,
    contentColor: Color,
) {
    val density = LocalDensity.current
    SideEffect {
        sheetState.density = density
    }
    SubcomposeLayout { constraints ->
        val layoutWidth = constraints.maxWidth
        val layoutHeight = constraints.maxHeight
        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

        val sheetPlaceable = subcompose(SinatraBottomSheetScaffoldLayoutSlot.Sheet) {
            bottomSheet(layoutHeight)
        }[0].measure(looseConstraints)

        val topBarPlaceable = topBar?.let {
            subcompose(SinatraBottomSheetScaffoldLayoutSlot.TopBar) { topBar() }[0]
                .measure(looseConstraints)
        }
        val topBarHeight = topBarPlaceable?.height ?: 0

        val bodyConstraints = looseConstraints.copy(maxHeight = layoutHeight - topBarHeight)
        val bodyPlaceable = subcompose(SinatraBottomSheetScaffoldLayoutSlot.Body) {
            Surface(
                modifier = modifier,
                color = containerColor,
                contentColor = contentColor,
            ) { body(PaddingValues(bottom = sheetPeekHeight)) }
        }[0].measure(bodyConstraints)

        val snackbarPlaceable = subcompose(SinatraBottomSheetScaffoldLayoutSlot.Snackbar, snackbarHost)[0]
            .measure(looseConstraints)

        layout(layoutWidth, layoutHeight) {
            val sheetOffsetY = sheetOffset().roundToInt()
            val sheetOffsetX = max(0, (layoutWidth - sheetPlaceable.width) / 2)

            val snackbarOffsetX = (layoutWidth - snackbarPlaceable.width) / 2
            val snackbarOffsetY = when (sheetState.currentValue) {
                SinatraSheetValue.PartiallyExpanded, SinatraSheetValue.HalfExpanded -> sheetOffsetY - snackbarPlaceable.height
                SinatraSheetValue.Expanded, SinatraSheetValue.Hidden -> layoutHeight - snackbarPlaceable.height
            }

            // Placement order is important for elevation
            bodyPlaceable.placeRelative(0, topBarHeight)
            topBarPlaceable?.placeRelative(0, 0)
            sheetPlaceable.placeRelative(sheetOffsetX, sheetOffsetY)
            snackbarPlaceable.placeRelative(snackbarOffsetX, snackbarOffsetY)
        }
    }
}

private enum class SinatraBottomSheetScaffoldLayoutSlot { TopBar, Body, Sheet, Snackbar }