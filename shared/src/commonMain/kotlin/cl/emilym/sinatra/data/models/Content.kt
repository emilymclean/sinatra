package cl.emilym.sinatra.data.models

fun pbToContentLink(
    contentLinks: List<cl.emilym.gtfs.content.ContentLink>,
    externalLinks: List<cl.emilym.gtfs.content.ExternalLink>,
    nativeLinks: List<cl.emilym.gtfs.content.NativeLink>,
): List<ContentLink> {
    return (contentLinks.map { ContentLink.Content(it.title, it.contentId, it.order ?: Int.MAX_VALUE) } +
            externalLinks.map { ContentLink.External(it.title, it.url, it.order ?: Int.MAX_VALUE) } +
            nativeLinks.map { ContentLink.Native(it.title, it.nativeReference, it.order ?: Int.MAX_VALUE) })
        .sortedBy { it.order }
}

data class Pages(
    val pages: List<Content>,
    val banner: Map<String, Alert>
)

data class Content(
    val id: ContentId,
    val title: String,
    val content: MarkdownString,
    val links: List<ContentLink>
) {

    companion object {
        fun fromPB(pb: cl.emilym.gtfs.content.Content): Content {
            return Content(
                pb.id,
                pb.title,
                pb.content,
                pbToContentLink(
                    pb.contentLinks,
                    pb.externalLinks,
                    pb.nativeLinks
                )
            )
        }
    }

}

sealed interface ContentLink {
    val title: String
    val order: Int

    data class External(
        override val title: String,
        val url: String,
        override val order: Int
    ): ContentLink
    data class Content(
        override val title: String,
        val id: ContentId,
        override val order: Int
    ): ContentLink
    data class Native(
        override val title: String,
        val nativeReference: String,
        override val order: Int
    ): ContentLink

    companion object {
        fun external(title: String, url: String?) : ContentLink? {
            return url?.let { External(title, it, 0) }
        }
    }
}