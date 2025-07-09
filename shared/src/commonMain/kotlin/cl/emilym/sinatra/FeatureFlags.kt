package cl.emilym.sinatra

import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import org.koin.mp.KoinPlatform
import kotlin.reflect.KProperty

private class EnumFeatureFlagDelegate(
    val flag: FeatureFlag
) {
    private val remoteConfigRepository by lazy { KoinPlatform.getKoin().get<RemoteConfigRepository>() }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        return remoteConfigRepository.featureImmediate(flag)
    }
}

enum class FeatureFlag(
    val default: Boolean,
    val overrideName: String? = null
) {
    ROUTE_DETAIL_CLICKABLE_STOPS(true),
    ROUTE_DETAIL_HIGHLIGHT_SOURCE_STOP(false),
    ROUTE_DETAIL_PREVENT_ZOOM_WHEN_HAVE_SOURCE_STOP(false),
    ROUTE_DETAIL_NEAREST_STOP(true),
    STOP_DETAIL_SHOW_ROUTE_FILTER(true),
    STOP_DETAIL_MANUALLY_ADJUST_PLATFORM_NAME(true),
    STOP_DETAIL_HIDE_PLATFORM_FOR_SYNTHETIC(true),
    STOP_DETAIL_CONCEAL_LIVENESS_STRING(true),
    STOP_DETAIL_LIVENESS_ICON(false),
    STOP_DETAIL_SHOW_ACCESSIBILITY(true),
    STOP_CARD_SHOW_ACCESSIBILITY(false),
    MAP_SEARCH_SCREEN_NEARBY_STOPS_SEARCH(true),
    NAVIGATE_ENTRY_SCREEN_FAVOURITE_SEARCH(true),
    IOS_APPLE_MAP_LOGO_FOLLOW_BOTTOM_SHEET(false),
    RAPTOR_ARRIVAL_BASED_ROUTING(true),
    RAPTOR_SWAP_BUTTON(true),
    NAVIGATE_BUTTON_TAB_BAR(true),
    SERVICE_ALERT_BUTTON_TAB_BAR(false),
    PLACE_DETAIL_ENABLED(true),
    FAVOURITE_NEARBY_STOP_HOME_SCREEN(true),
    NEW_SERVICE_HOME_SCREEN(true),
    QUICK_NAVIGATION_HOME_SCREEN(true),
    QUICK_ADD_FAVOURITE_HOME_SCREEN(true),
    SPECIFY_TIMEZONE_WHEN_DIFFERENT(true),
    HIDE_MAPS_FROM_ACCESSIBILITY(false),
    HOLD_MAP_POINT_DETAIL(true),
    GLOBAL_HIDE_TRANSPORT_ACCESSIBILITY(false);

    val immediate by EnumFeatureFlagDelegate(this)
}

val FeatureFlag.flagName: String
    get() = overrideName ?: this.name.lowercase()