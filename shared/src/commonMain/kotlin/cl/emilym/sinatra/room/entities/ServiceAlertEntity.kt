package cl.emilym.sinatra.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cl.emilym.sinatra.data.models.ServiceAlert
import cl.emilym.sinatra.data.models.ServiceAlertRegion
import kotlinx.datetime.Instant
import kotlin.time.Duration

@Entity
data class ServiceAlertEntity(
    @PrimaryKey val id: String,
    val title: String,
    val url: String?,
    val date: Long?,
    val regions: String,
    @ColumnInfo(defaultValue = "null")
    val highlightDuration: String?,
    @ColumnInfo(defaultValue = "false")
    val viewed: Boolean
) {

    fun toModel(): ServiceAlert {
        return ServiceAlert(
            id,
            title,
            url,
            date?.let { Instant.fromEpochMilliseconds(date) },
            when (regions) {
                "" -> listOf()
                else -> regions.split(",").map { ServiceAlertRegion.valueOf(it) }
            },
            highlightDuration?.let { Duration.parse(it) }
        )
    }

    companion object {
        fun fromModel(model: ServiceAlert): ServiceAlertEntity {
            return ServiceAlertEntity(
                model.id,
                model.title,
                model.url,
                model.date?.toEpochMilliseconds(),
                model.regions.joinToString(",") { it.name },
                model.highlightDuration?.toIsoString(),
                model.viewed
            )
        }
    }

}