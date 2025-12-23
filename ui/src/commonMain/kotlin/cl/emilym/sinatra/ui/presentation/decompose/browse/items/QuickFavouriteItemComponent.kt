package cl.emilym.sinatra.ui.presentation.decompose.browse.items

import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponent
import cl.emilym.sinatra.ui.presentation.decompose.base.SinatraComponentContext
import cl.emilym.sinatra.ui.presentation.decompose.main.NavigationInstruction

interface QuickFavouriteItemComponent: SinatraComponent

class DefaultQuickFavouriteItemComponent(
    onNavigate: (NavigationInstruction) -> Unit,
    sinatraComponentContext: SinatraComponentContext
): QuickFavouriteItemComponent, SinatraComponentContext by sinatraComponentContext