package cl.emilym.sinatra.ui.maps

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun uuid(): String {
    return Uuid.random().toHexString()
}