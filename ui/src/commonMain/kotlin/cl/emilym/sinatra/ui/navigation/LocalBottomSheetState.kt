package cl.emilym.sinatra.ui.navigation

import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.staticCompositionLocalOf

@OptIn(ExperimentalMaterial3Api::class)
val LocalBottomSheetState = staticCompositionLocalOf<BottomSheetScaffoldState> { error("View is not in a bottom sheet") }