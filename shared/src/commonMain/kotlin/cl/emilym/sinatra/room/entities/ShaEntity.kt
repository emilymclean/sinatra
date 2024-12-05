package cl.emilym.sinatra.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import cl.emilym.sinatra.data.models.ShaDigest

@Entity
data class ShaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sha: ShaDigest,
    val type: String,
    val resource: String,
    val added: Long,
)