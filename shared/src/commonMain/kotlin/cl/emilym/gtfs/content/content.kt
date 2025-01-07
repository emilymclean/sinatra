@file:OptIn(pbandk.PublicForGeneratedCode::class)

package cl.emilym.gtfs.content

@pbandk.Export
public data class Pages(
    val pages: List<cl.emilym.gtfs.content.Content> = emptyList(),
    val banners: Map<String?, cl.emilym.gtfs.content.Banner?> = emptyMap(),
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
            fields = buildList(2) {
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
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "banners",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Map<String?, cl.emilym.gtfs.content.Banner?>(keyType = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true), valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.content.Banner.Companion)),
                        jsonName = "banners",
                        value = cl.emilym.gtfs.content.Pages::banners
                    )
                )
            }
        )
    }

    public data class BannersEntry(
        override val key: String? = null,
        override val value: cl.emilym.gtfs.content.Banner? = null,
        override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
    ) : pbandk.Message, Map.Entry<String?, cl.emilym.gtfs.content.Banner?> {
        override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.content.Pages.BannersEntry = protoMergeImpl(other)
        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.content.Pages.BannersEntry> get() = Companion.descriptor
        override val protoSize: Int by lazy { super.protoSize }
        public companion object : pbandk.Message.Companion<cl.emilym.gtfs.content.Pages.BannersEntry> {
            public val defaultInstance: cl.emilym.gtfs.content.Pages.BannersEntry by lazy { cl.emilym.gtfs.content.Pages.BannersEntry() }
            override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.content.Pages.BannersEntry = cl.emilym.gtfs.content.Pages.BannersEntry.decodeWithImpl(u)

            override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.content.Pages.BannersEntry> = pbandk.MessageDescriptor(
                fullName = "content.Pages.BannersEntry",
                messageClass = cl.emilym.gtfs.content.Pages.BannersEntry::class,
                messageCompanion = this,
                fields = buildList(2) {
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "key",
                            number = 1,
                            type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                            jsonName = "key",
                            value = cl.emilym.gtfs.content.Pages.BannersEntry::key
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "value",
                            number = 2,
                            type = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.content.Banner.Companion),
                            jsonName = "value",
                            value = cl.emilym.gtfs.content.Pages.BannersEntry::value
                        )
                    )
                }
            )
        }
    }
}

@pbandk.Export
public data class Content(
    val id: String,
    val title: String,
    val content: String,
    val externalLinks: List<cl.emilym.gtfs.content.ExternalLink> = emptyList(),
    val contentLinks: List<cl.emilym.gtfs.content.ContentLink> = emptyList(),
    val nativeLinks: List<cl.emilym.gtfs.content.NativeLink> = emptyList(),
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
            fields = buildList(6) {
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
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "nativeLinks",
                        number = 6,
                        type = pbandk.FieldDescriptor.Type.Repeated<cl.emilym.gtfs.content.NativeLink>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.content.NativeLink.Companion)),
                        jsonName = "nativeLinks",
                        value = cl.emilym.gtfs.content.Content::nativeLinks
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
    val order: Int? = null,
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
            fields = buildList(3) {
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
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "order",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt32(hasPresence = true),
                        jsonName = "order",
                        value = cl.emilym.gtfs.content.ExternalLink::order
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
    val order: Int? = null,
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
            fields = buildList(3) {
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
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "order",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt32(hasPresence = true),
                        jsonName = "order",
                        value = cl.emilym.gtfs.content.ContentLink::order
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class NativeLink(
    val title: String,
    val nativeReference: String,
    val order: Int? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.content.NativeLink = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.content.NativeLink> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.content.NativeLink> {
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.content.NativeLink = cl.emilym.gtfs.content.NativeLink.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.content.NativeLink> = pbandk.MessageDescriptor(
            fullName = "content.NativeLink",
            messageClass = cl.emilym.gtfs.content.NativeLink::class,
            messageCompanion = this,
            fields = buildList(3) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "title",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "title",
                        value = cl.emilym.gtfs.content.NativeLink::title
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "nativeReference",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "nativeReference",
                        value = cl.emilym.gtfs.content.NativeLink::nativeReference
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "order",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt32(hasPresence = true),
                        jsonName = "order",
                        value = cl.emilym.gtfs.content.NativeLink::order
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class PageLink(
    val title: String,
    val nativeReference: String,
    val order: Int? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.content.PageLink = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.content.PageLink> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.content.PageLink> {
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.content.PageLink = cl.emilym.gtfs.content.PageLink.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.content.PageLink> = pbandk.MessageDescriptor(
            fullName = "content.PageLink",
            messageClass = cl.emilym.gtfs.content.PageLink::class,
            messageCompanion = this,
            fields = buildList(3) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "title",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "title",
                        value = cl.emilym.gtfs.content.PageLink::title
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "nativeReference",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "nativeReference",
                        value = cl.emilym.gtfs.content.PageLink::nativeReference
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "order",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt32(hasPresence = true),
                        jsonName = "order",
                        value = cl.emilym.gtfs.content.PageLink::order
                    )
                )
            }
        )
    }
}

@pbandk.Export
public data class Banner(
    val title: String,
    val message: String? = null,
    val severity: String? = null,
    val externalLinks: List<cl.emilym.gtfs.content.ExternalLink> = emptyList(),
    val contentLinks: List<cl.emilym.gtfs.content.ContentLink> = emptyList(),
    val nativeLinks: List<cl.emilym.gtfs.content.NativeLink> = emptyList(),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): cl.emilym.gtfs.content.Banner = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.content.Banner> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<cl.emilym.gtfs.content.Banner> {
        override fun decodeWith(u: pbandk.MessageDecoder): cl.emilym.gtfs.content.Banner = cl.emilym.gtfs.content.Banner.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<cl.emilym.gtfs.content.Banner> = pbandk.MessageDescriptor(
            fullName = "content.Banner",
            messageClass = cl.emilym.gtfs.content.Banner::class,
            messageCompanion = this,
            fields = buildList(6) {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "title",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "title",
                        value = cl.emilym.gtfs.content.Banner::title
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "message",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "message",
                        value = cl.emilym.gtfs.content.Banner::message
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "severity",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "severity",
                        value = cl.emilym.gtfs.content.Banner::severity
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "externalLinks",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Repeated<cl.emilym.gtfs.content.ExternalLink>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.content.ExternalLink.Companion)),
                        jsonName = "externalLinks",
                        value = cl.emilym.gtfs.content.Banner::externalLinks
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "contentLinks",
                        number = 5,
                        type = pbandk.FieldDescriptor.Type.Repeated<cl.emilym.gtfs.content.ContentLink>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.content.ContentLink.Companion)),
                        jsonName = "contentLinks",
                        value = cl.emilym.gtfs.content.Banner::contentLinks
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "nativeLinks",
                        number = 6,
                        type = pbandk.FieldDescriptor.Type.Repeated<cl.emilym.gtfs.content.NativeLink>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = cl.emilym.gtfs.content.NativeLink.Companion)),
                        jsonName = "nativeLinks",
                        value = cl.emilym.gtfs.content.Banner::nativeLinks
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
        banners = banners + plus.banners,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun Pages.Companion.decodeWithImpl(u: pbandk.MessageDecoder): Pages {
    var pages: pbandk.ListWithSize.Builder<cl.emilym.gtfs.content.Content>? = null
    var banners: pbandk.MessageMap.Builder<String?, cl.emilym.gtfs.content.Banner?>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> pages = (pages ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<cl.emilym.gtfs.content.Content> }
            2 -> banners = (banners ?: pbandk.MessageMap.Builder()).apply { this.entries += _fieldValue as kotlin.sequences.Sequence<pbandk.MessageMap.Entry<String?, cl.emilym.gtfs.content.Banner?>> }
        }
    }

    return Pages(pbandk.ListWithSize.Builder.fixed(pages), pbandk.MessageMap.Builder.fixed(banners), unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForPagesBannersEntry")
public fun Pages.BannersEntry?.orDefault(): cl.emilym.gtfs.content.Pages.BannersEntry = this ?: Pages.BannersEntry.defaultInstance

private fun Pages.BannersEntry.protoMergeImpl(plus: pbandk.Message?): Pages.BannersEntry = (plus as? Pages.BannersEntry)?.let {
    it.copy(
        key = plus.key ?: key,
        value = value?.plus(plus.value) ?: plus.value,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun Pages.BannersEntry.Companion.decodeWithImpl(u: pbandk.MessageDecoder): Pages.BannersEntry {
    var key: String? = null
    var value: cl.emilym.gtfs.content.Banner? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> key = _fieldValue as String
            2 -> value = _fieldValue as cl.emilym.gtfs.content.Banner
        }
    }

    return Pages.BannersEntry(key, value, unknownFields)
}

private fun Content.protoMergeImpl(plus: pbandk.Message?): Content = (plus as? Content)?.let {
    it.copy(
        externalLinks = externalLinks + plus.externalLinks,
        contentLinks = contentLinks + plus.contentLinks,
        nativeLinks = nativeLinks + plus.nativeLinks,
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
    var nativeLinks: pbandk.ListWithSize.Builder<cl.emilym.gtfs.content.NativeLink>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> id = _fieldValue as String
            2 -> title = _fieldValue as String
            3 -> content = _fieldValue as String
            4 -> externalLinks = (externalLinks ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<cl.emilym.gtfs.content.ExternalLink> }
            5 -> contentLinks = (contentLinks ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<cl.emilym.gtfs.content.ContentLink> }
            6 -> nativeLinks = (nativeLinks ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<cl.emilym.gtfs.content.NativeLink> }
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
        pbandk.ListWithSize.Builder.fixed(contentLinks), pbandk.ListWithSize.Builder.fixed(nativeLinks), unknownFields)
}

private fun ExternalLink.protoMergeImpl(plus: pbandk.Message?): ExternalLink = (plus as? ExternalLink)?.let {
    it.copy(
        order = plus.order ?: order,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun ExternalLink.Companion.decodeWithImpl(u: pbandk.MessageDecoder): ExternalLink {
    var title: String? = null
    var url: String? = null
    var order: Int? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> title = _fieldValue as String
            2 -> url = _fieldValue as String
            3 -> order = _fieldValue as Int
        }
    }

    if (title == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("title")
    }
    if (url == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("url")
    }
    return ExternalLink(title!!, url!!, order, unknownFields)
}

private fun ContentLink.protoMergeImpl(plus: pbandk.Message?): ContentLink = (plus as? ContentLink)?.let {
    it.copy(
        order = plus.order ?: order,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun ContentLink.Companion.decodeWithImpl(u: pbandk.MessageDecoder): ContentLink {
    var title: String? = null
    var contentId: String? = null
    var order: Int? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> title = _fieldValue as String
            2 -> contentId = _fieldValue as String
            3 -> order = _fieldValue as Int
        }
    }

    if (title == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("title")
    }
    if (contentId == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("contentId")
    }
    return ContentLink(title!!, contentId!!, order, unknownFields)
}

private fun NativeLink.protoMergeImpl(plus: pbandk.Message?): NativeLink = (plus as? NativeLink)?.let {
    it.copy(
        order = plus.order ?: order,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun NativeLink.Companion.decodeWithImpl(u: pbandk.MessageDecoder): NativeLink {
    var title: String? = null
    var nativeReference: String? = null
    var order: Int? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> title = _fieldValue as String
            2 -> nativeReference = _fieldValue as String
            3 -> order = _fieldValue as Int
        }
    }

    if (title == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("title")
    }
    if (nativeReference == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("nativeReference")
    }
    return NativeLink(title!!, nativeReference!!, order, unknownFields)
}

private fun PageLink.protoMergeImpl(plus: pbandk.Message?): PageLink = (plus as? PageLink)?.let {
    it.copy(
        order = plus.order ?: order,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun PageLink.Companion.decodeWithImpl(u: pbandk.MessageDecoder): PageLink {
    var title: String? = null
    var nativeReference: String? = null
    var order: Int? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> title = _fieldValue as String
            2 -> nativeReference = _fieldValue as String
            3 -> order = _fieldValue as Int
        }
    }

    if (title == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("title")
    }
    if (nativeReference == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("nativeReference")
    }
    return PageLink(title!!, nativeReference!!, order, unknownFields)
}

private fun Banner.protoMergeImpl(plus: pbandk.Message?): Banner = (plus as? Banner)?.let {
    it.copy(
        message = plus.message ?: message,
        severity = plus.severity ?: severity,
        externalLinks = externalLinks + plus.externalLinks,
        contentLinks = contentLinks + plus.contentLinks,
        nativeLinks = nativeLinks + plus.nativeLinks,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun Banner.Companion.decodeWithImpl(u: pbandk.MessageDecoder): Banner {
    var title: String? = null
    var message: String? = null
    var severity: String? = null
    var externalLinks: pbandk.ListWithSize.Builder<cl.emilym.gtfs.content.ExternalLink>? = null
    var contentLinks: pbandk.ListWithSize.Builder<cl.emilym.gtfs.content.ContentLink>? = null
    var nativeLinks: pbandk.ListWithSize.Builder<cl.emilym.gtfs.content.NativeLink>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> title = _fieldValue as String
            2 -> message = _fieldValue as String
            3 -> severity = _fieldValue as String
            4 -> externalLinks = (externalLinks ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<cl.emilym.gtfs.content.ExternalLink> }
            5 -> contentLinks = (contentLinks ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<cl.emilym.gtfs.content.ContentLink> }
            6 -> nativeLinks = (nativeLinks ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<cl.emilym.gtfs.content.NativeLink> }
        }
    }

    if (title == null) {
        throw pbandk.InvalidProtocolBufferException.missingRequiredField("title")
    }
    return Banner(title!!, message, severity, pbandk.ListWithSize.Builder.fixed(externalLinks),
        pbandk.ListWithSize.Builder.fixed(contentLinks), pbandk.ListWithSize.Builder.fixed(nativeLinks), unknownFields)
}
