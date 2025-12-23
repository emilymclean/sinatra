package cl.emilym.sinatra.ui.presentation.decompose.browse.items

import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponent
import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponentContext
import cl.emilym.sinatra.ui.presentation.decompose.main.Navigation

interface ServiceUpdateItemComponent: SinatraComponent

class DefaultServiceUpdateItemComponent(
    onNavigate: (Navigation) -> Unit,
    sinatraComponentContext: SinatraComponentContext
): ServiceUpdateItemComponent, SinatraComponentContext by sinatraComponentContext