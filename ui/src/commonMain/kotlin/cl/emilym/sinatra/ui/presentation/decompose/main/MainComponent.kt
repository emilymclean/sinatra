package cl.emilym.sinatra.ui.presentation.decompose.main

import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponent
import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponentContext

interface MainComponent: SinatraComponent

class DefaultMainComponent(
    sinatraComponentContext: SinatraComponentContext
): MainComponent, SinatraComponentContext by sinatraComponentContext {

}