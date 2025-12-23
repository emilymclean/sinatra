package cl.emilym.sinatra.ui.presentation.decompose.browse.items

import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponent
import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponentContext
import cl.emilym.sinatra.ui.presentation.decompose.main.Navigation

interface NearbyDepartureItemComponent: SinatraComponent

class DefaultNearbyDepartureItemComponent(
    onNavigate: (Navigation) -> Unit,
    sinatraComponentContext: SinatraComponentContext
): NearbyDepartureItemComponent, SinatraComponentContext by sinatraComponentContext