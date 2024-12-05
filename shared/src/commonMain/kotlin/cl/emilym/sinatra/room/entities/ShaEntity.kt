package cl.emilym.sinatra.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import cl.emilym.sinatra.data.models.ShaDigest

const val TYPE_STOP = "stop"
const val TYPE_STOP_TIMETABLE = "stop/timetable"
const val TYPE_ROUTE = "route"
const val TYPE_SERVICE = "service"

@Entity
data class ShaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sha: ShaDigest,
    val type: String,
    val resource: String,
)