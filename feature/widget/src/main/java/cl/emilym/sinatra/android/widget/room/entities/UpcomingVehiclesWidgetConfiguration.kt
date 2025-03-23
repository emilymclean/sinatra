package cl.emilym.sinatra.android.widget.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UpcomingVehiclesWidgetConfiguration(
    @PrimaryKey
    val widgetId: Int,
    val stopId: String,
    val routeId: String?,
    val heading: String?,
)