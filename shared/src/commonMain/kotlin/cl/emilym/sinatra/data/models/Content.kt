package cl.emilym.sinatra.data.models

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
                pb.contentLinks.map { ContentLink.Content(it.title, it.contentId) } +
                        pb.externalLinks.map { ContentLink.External(it.title, it.url) }
            )
        }
    }

}

sealed interface ContentLink {
    val title: String

    data class External(
        override val title: String,
        val url: String
    ): ContentLink
    data class Content(
        override val title: String,
        val id: ContentId
    ): ContentLink

    companion object {
        fun external(title: String, url: String?) : ContentLink? {
            return url?.let { External(title, it) }
        }
    }
}