package cl.emilym.sinatra.component

import cl.emilym.sinatra.component.base.SinatraChild
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value

interface MapComponent<FAB: MapFabChild, SHEET: MapSheetChild> {

    val fabChild: Value<ChildSlot<*, FAB>>
    val sheetChild: Value<ChildSlot<*, SHEET>>

}

interface MapFabChild: SinatraChild
interface MapSheetChild: SinatraChild