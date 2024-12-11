package cl.emilym.sinatra.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavouriteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val itemType: String,
    val foreignId: String,
)