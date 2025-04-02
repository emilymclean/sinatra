package cl.emilym.sinatra

import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import org.koin.mp.KoinPlatform
import kotlin.reflect.KProperty

private class FeatureFlagDelegate(
    val default: Boolean = true,
    val name: String? = null
) {

    private val remoteConfigRepository by lazy { KoinPlatform.getKoin().get<RemoteConfigRepository>() }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        return remoteConfigRepository.featureImmediate(name ?: property.name.lowercase(), default)
    }

}

object FeatureFlags {
    val ROUTE_DETAIL_CLICKABLE_STOPS by FeatureFlagDelegate(true)
    val ROUTE_DETAIL_HIGHLIGHT_SOURCE_STOP by FeatureFlagDelegate(false)
    val ROUTE_DETAIL_PREVENT_ZOOM_WHEN_HAVE_SOURCE_STOP by FeatureFlagDelegate(false)
    val ROUTE_DETAIL_NEAREST_STOP by FeatureFlagDelegate(true)
    val STOP_DETAIL_SHOW_IN_MAPS_BUTTON by FeatureFlagDelegate(false)
    val STOP_DETAIL_SHOW_ACCESSIBILITY by FeatureFlagDelegate(true)
    val STOP_CARD_SHOW_ACCESSIBILITY by FeatureFlagDelegate(false)
    val MAP_SEARCH_SCREEN_NEARBY_STOPS_SEARCH by FeatureFlagDelegate(true)
    val IOS_APPLE_MAP_LOGO_FOLLOW_BOTTOM_SHEET by FeatureFlagDelegate(false)
    val RAPTOR_ARRIVAL_BASED_ROUTING by FeatureFlagDelegate(true)
    val RAPTOR_SWAP_BUTTON by FeatureFlagDelegate(true)
    val NAVIGATE_BUTTON_TAB_BAR by FeatureFlagDelegate(true)
    val PLACE_DETAIL_ENABLED by FeatureFlagDelegate(true)
    val SPECIFY_TIMEZONE_WHEN_DIFFERENT by FeatureFlagDelegate(true)
    val HIDE_MAPS_FROM_ACCESSIBILITY by FeatureFlagDelegate(false)
}