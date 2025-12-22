package cl.emilym.sinatra.ui.presentation.decompose.outofdate

import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponentContext

interface AppOutOfDateComponent

class DefaultAppOutOfDateComponent(
    componentContext: SinatraComponentContext
): AppOutOfDateComponent, SinatraComponentContext by componentContext