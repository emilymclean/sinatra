@file:OptIn(pbandk.PublicForGeneratedCode::class)

package cl.emilym.gtfs.content

@pbandk.Export
public data class Pages(
    val pages: List<cl.emilym.gtfs.content.Content> = emptyList(),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.content.Pages = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.content.Pages> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.content.Pages> {
        public val defaultInstance: cl.emilym.gtfs.content.Pages by lazy { cl.emilym.gtfs.content.Pages() }
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.content.Pages = cl.emilym.gtfs.content.Pages.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.content.Pages> = pbandk.MessageDescriptor(
            fullName = "content.Pages",
            messageClass = cl.emilym.gtfs.content.Pages::class,
            messageCompanion = this,
            fields = buildList(1) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "pages",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Repeated<cl.emilym.gtfs.content.Content>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.content.Content.Companion)),
                        jsonName = "pages",
                        value = cl.emilym.gtfs.content.Pages::pages
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class Content(
    val id: String,
    val title: String,
    val content: String,
    val externalLinks: List<cl.emilym.gtfs.content.ExternalLink> = emptyList(),
    val contentLinks: List<cl.emilym.gtfs.content.ContentLink> = emptyList(),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.content.Content = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.content.Content> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.content.Content> {
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.content.Content = cl.emilym.gtfs.content.Content.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.content.Content> = pbandk.MessageDescriptor(
            fullName = "content.Content",
            messageClass = cl.emilym.gtfs.content.Content::class,
            messageCompanion = this,
            fields = buildList(5) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "id",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "id",
                        value = cl.emilym.gtfs.content.Content::id
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "title",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "title",
                        value = cl.emilym.gtfs.content.Content::title
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "content",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "content",
                        value = cl.emilym.gtfs.content.Content::content
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "externalLinks",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Repeated<cl.emilym.gtfs.content.ExternalLink>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.content.ExternalLink.Companion)),
                        jsonName = "externalLinks",
                        value = cl.emilym.gtfs.content.Content::externalLinks
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "contentLinks",
                        number = 5,
                        type = pbandk.FieldDescriptor.Type.Repeated<cl.emilym.gtfs.content.ContentLink>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.content.ContentLink.Companion)),
                        jsonName = "contentLinks",
                        value = cl.emilym.gtfs.content.Content::contentLinks
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class ExternalLink(
    val title: String,
    val url: String,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.content.ExternalLink = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.content.ExternalLink> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.content.ExternalLink> {
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.content.ExternalLink = cl.emilym.gtfs.content.ExternalLink.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.content.ExternalLink> = pbandk.MessageDescriptor(
            fullName = "content.ExternalLink",
            messageClass = cl.emilym.gtfs.content.ExternalLink::class,
            messageCompanion = this,
            fields = buildList(2) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "title",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "title",
                        value = cl.emilym.gtfs.content.ExternalLink::title
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "url",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "url",
                        value = cl.emilym.gtfs.content.ExternalLink::url
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class ContentLink(
    val title: String,
    val contentId: String,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.content.ContentLink = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.content.ContentLink> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.content.ContentLink> {
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.content.ContentLink = cl.emilym.gtfs.content.ContentLink.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.content.ContentLink> = pbandk.MessageDescriptor(
            fullName = "content.ContentLink",
            messageClass = cl.emilym.gtfs.content.ContentLink::class,
            messageCompanion = this,
            fields = buildList(2) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "title",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "title",
                        value = cl.emilym.gtfs.content.ContentLink::title
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "contentId",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "contentId",
                        value = cl.emilym.gtfs.content.ContentLink::contentId
                    )
                )
            }
        )
    }
}

@pbandk.Export
@pbandk.JsName("orDefaultForPages")
public fun Pages?.orDefault(): cl.emilym.gtfs.content.Pages = this ?: Pages.defaultInstance

private fun Pages.protoMergeImpl(plus: pbandk.Message?): Pages = (plus as? Pages)?.let {
    it.copy(
        pages = pages + plus.pages,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun Pages.Companion.decodeWithImpl(u: pbandk.MessageDecoder): Pages {
    var pages: pbandk.ListWithSize.Builder<cl.emilym.gtfs.content.Content>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> pages = (pages ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<cl.emilym.gtfs.content.Content> }
        }
    }

    return Pages(pbandk.ListWithSize.Builder.fixed(pages), unknownFields)
}

private fun Content.protoMergeImpl(plus: pbandk.Message?): Content = (plus as? Content)?.let {
    it.copy(
        externalLinks = externalLinks + plus.externalLinks,
        contentLinks = contentLinks + plus.contentLinks,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun Content.Companion.decodeWithImpl(u: pbandk.MessageDecoder): Content {
    var id: String? = null
    var title: String? = null
    var content: String? = null
    var externalLinks: pbandk.ListWithSize.Builder<cl.emilym.gtfs.content.ExternalLink>? = null
    var contentLinks: pbandk.ListWithSize.Builder<cl.emilym.gtfs.content.ContentLink>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> id = _fieldValue as String
            2 -> title = _fieldValue as String
            3 -> content = _fieldValue as String
            4 -> externalLinks = (externalLinks ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<cl.emilym.gtfs.content.ExternalLink> }
            5 -> contentLinks = (contentLinks ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<cl.emilym.gtfs.content.ContentLink> }
        }
    }

    if (id == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("id")
    }
    if (title == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("title")
    }
    if (content == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("content")
    }
    return Content(id!!, title!!, content!!, pbandk.ListWithSize.Builder.fixed(externalLinks),
        pbandk.ListWithSize.Builder.fixed(contentLinks), unknownFields)
}

private fun ExternalLink.protoMergeImpl(plus: pbandk.Message?): ExternalLink = (plus as? ExternalLink)?.let {
    it.copy(
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun ExternalLink.Companion.decodeWithImpl(u: pbandk.MessageDecoder): ExternalLink {
    var title: String? = null
    var url: String? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> title = _fieldValue as String
            2 -> url = _fieldValue as String
        }
    }

    if (title == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("title")
    }
    if (url == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("url")
    }
    return ExternalLink(title!!, url!!, unknownFields)
}

private fun ContentLink.protoMergeImpl(plus: pbandk.Message?): ContentLink = (plus as? ContentLink)?.let {
    it.copy(
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun ContentLink.Companion.decodeWithImpl(u: pbandk.MessageDecoder): ContentLink {
    var title: String? = null
    var contentId: String? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> title = _fieldValue as String
            2 -> contentId = _fieldValue as String
        }
    }

    if (title == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("title")
    }
    if (contentId == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("contentId")
    }
    return ContentLink(title!!, contentId!!, unknownFields)
}
