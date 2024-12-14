package cl.emilym.sinatra.data.models

data class Content(
    val title: String,
    val content: MarkdownString,
    val links: List<ContentLink>
)

sealed interface ContentLink {
    val title: String

    data class External(
        override val title: String,
        val url: String
    ): ContentLink

    companion object {
        fun external(title: String, url: String?) : ContentLink? {
            return url?.let { External(title, it) }
        }
    }
}