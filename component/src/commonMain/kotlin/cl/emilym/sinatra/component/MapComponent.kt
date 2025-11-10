package cl.emilym.sinatra.component

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value

interface MapComponent {

    val fabChild: Value<ChildSlot<*, MapFabChild>>
    val sheetChild: Value<ChildSlot<*, MapSheetChild>>

}

interface MapFabChild: SinatraChild
interface MapSheetChild: SinatraChild