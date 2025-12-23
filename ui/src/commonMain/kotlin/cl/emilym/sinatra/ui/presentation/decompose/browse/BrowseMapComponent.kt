package cl.emilym.sinatra.ui.presentation.decompose.browse

import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponent
import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponentContext

interface BrowseMapComponent: SinatraComponent

class DefaultBrowseMapComponent(
    sinatraComponentContext: SinatraComponentContext
): BrowseMapComponent, SinatraComponentContext by sinatraComponentContext