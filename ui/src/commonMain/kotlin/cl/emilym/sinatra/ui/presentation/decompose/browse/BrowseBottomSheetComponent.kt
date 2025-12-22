package cl.emilym.sinatra.ui.presentation.decompose.browse

import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponent
import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponentContext

interface BrowseBottomSheetComponent: SinatraComponent

class DefaultBrowseBottomSheetComponent(
    sinatraComponentContext: SinatraComponentContext
): BrowseBottomSheetComponent, SinatraComponentContext by sinatraComponentContext