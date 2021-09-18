package com.weilok.rssocto.data

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Path
import org.simpleframework.xml.Root

@Root(name = "feed", strict = false)
data class AtomFeed @JvmOverloads constructor(
    @field:Element(name = "id")
    @param:Element(name = "id")
    var url: String? = null,

    @field:Element(name = "title")
    @param:Element(name = "title")
    var title: String? = null,

    @field:ElementList(name = "entry", inline = true, required = false)
    @param:ElementList(name = "entry", inline = true, required = false)
    var entryList: List<AtomEntry>? = null
) {
    @Root(name = "entry")
    data class AtomEntry @JvmOverloads constructor(
        @field:Element(name = "url")
        @param:Element(name = "url")
        var url: String? = null,

        @field:Element(name = "title")
        @param:Element(name = "title")
        var title: String? = null,

        @field:Element(name = "published")
        @param:Element(name = "published")
        var date: String? = null,

        @field:Element(name = "name")
        @param:Element(name = "name")
        @field:Path("author")
        @param:Path("author")
        var author: String? = null,

        @field:Element(name = "content")
        @param:Element(name = "content")
        var content: String? = null
    )
}