package cl.emilym.sinatra.room.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import cl.emilym.sinatra.data.models.Content
import cl.emilym.sinatra.data.models.ContentLink

@Entity
data class ContentEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String,
) {

    companion object {
        fun fromModel(
            content: Content
        ): ContentEntity {
            return ContentEntity(
                content.id,
                content.title,
                content.content
            )
        }
    }

}

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ContentEntity::class,
            parentColumns = ["id"],
            childColumns = ["contentId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class ContentLinkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val contentId: String,
    val type: String,
    val title: String,
    val ref: String,
    val order: Int
) {

    companion object {
        const val CONTENT_TYPE = "CONTENT"
        const val NATIVE_TYPE = "NATIVE"
        const val EXTERNAL_TYPE = "EXTERNAL"

        fun fromModel(
            content: Content
        ): List<ContentLinkEntity> {
            return content.links.mapNotNull {
                ContentLinkEntity(
                    0,
                    content.id,
                    content.title,
                    when (it) {
                        is ContentLink.Content -> CONTENT_TYPE
                        is ContentLink.Native -> NATIVE_TYPE
                        is ContentLink.External -> EXTERNAL_TYPE
                        else -> return@mapNotNull null
                    },
                    when (it) {
                        is ContentLink.Content -> it.id
                        is ContentLink.Native -> it.nativeReference
                        is ContentLink.External -> it.url
                        else -> return@mapNotNull null
                    },
                    it.order
                )
            }
        }
    }

}

data class ContentEntityWithContentLinkEntity(
    @Embedded val content: ContentEntity,
    @Relation(
        parentColumn = "contentId",
        entityColumn = "id"
    )
    val links: List<ContentLinkEntity>
) {

    fun toModel(): Content {
        return Content(
            content.id,
            content.title,
            content.content,
            links.mapNotNull {
                when (it.type) {
                    ContentLinkEntity.CONTENT_TYPE -> ContentLink.Content(
                        it.title,
                        it.ref,
                        it.order
                    )
                    ContentLinkEntity.NATIVE_TYPE -> ContentLink.Native(
                        it.title,
                        it.ref,
                        it.order
                    )
                    ContentLinkEntity.EXTERNAL_TYPE -> ContentLink.External(
                        it.title,
                        it.ref,
                        it.order
                    )
                    else -> return@mapNotNull null
                }
            }
        )
    }

}