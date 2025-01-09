package cl.emilym.sinatra.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.staticCompositionLocalOf
import cl.emilym.sinatra.ui.widgets.bottomsheet.SinatraBottomSheetScaffoldState

@OptIn(ExperimentalMaterial3Api::class)
val LocalBottomSheetState = staticCompositionLocalOf<SinatraBottomSheetScaffoldState?> { null }