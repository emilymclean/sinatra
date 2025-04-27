package cl.emilym.sinatra.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import cl.emilym.sinatra.ui.navigation.LocalBottomSheetState
import cl.emilym.sinatra.ui.widgets.bottomsheet.SinatraSheetState
import cl.emilym.sinatra.ui.widgets.bottomsheet.SinatraSheetValue

@Composable
fun rememberBottomSheetPosition(
    bottomSheetState: SinatraSheetState? = LocalBottomSheetState.current?.bottomSheetState
) {
    val lastBottomSheetState = rememberSaveable(bottomSheetState?.currentValue) {
        bottomSheetState?.currentValue ?:
        SinatraSheetValue.PartiallyExpanded
    }
    LaunchedEffect(Unit) {
        when (lastBottomSheetState) {
            SinatraSheetValue.PartiallyExpanded -> bottomSheetState?.partialExpand()
            SinatraSheetValue.HalfExpanded -> bottomSheetState?.halfExpand()
            SinatraSheetValue.Expanded -> bottomSheetState?.expand()
            SinatraSheetValue.Hidden -> bottomSheetState?.hide()
        }
    }
}