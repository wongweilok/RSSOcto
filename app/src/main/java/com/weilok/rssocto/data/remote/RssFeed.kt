/*
    Copyright (C) 2021 Wong Wei Lok <wongweilok@disroot.org>

    This file is part of RSSOcto

    RSSOcto is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    RSSOcto is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this RSSOcto.  If not, see <https://www.gnu.org/licenses/>.
*/

package com.weilok.rssocto.data.remote

import org.simpleframework.xml.*

@Root(name = "rss", strict = false)
data class RssFeed @JvmOverloads constructor(
    @field:ElementList(name = "link", inline = true, required = false)
    @param:ElementList(name = "link", inline = true, required = false)
    @field:Path("channel")
    @param:Path("channel")
    var urlList: List<Link>? = null,

    @field:Element(name = "title")
    @param:Element(name = "title")
    @field:Path("channel")
    @param:Path("channel")
    var title: String? = null,

    @field:Element(name = "url", required = false)
    @param:Element(name = "url", required = false)
    @field:Path("channel/image")
    @param:Path("channel/image")
    var imageUrl: String? = "",

    @field:ElementList(name = "item", inline = true, required = false)
    @param:ElementList(name = "item", inline = true, required = false)
    @field:Path("channel")
    @param:Path("channel")
    var entryList: List<RssEntry>? = null
) {
    @Root(name = "item", strict = false)
    data class RssEntry @JvmOverloads constructor(
        @field:Element(name = "guid")
        @param:Element(name = "guid")
        var url: String? = null,

        @field:Element(name = "title")
        @param:Element(name = "title")
        var title: String? = null,

        @field:Element(name = "pubDate")
        @param:Element(name = "pubDate")
        var date: String? = null,

        @field:Element(name = "creator", required = false)
        @param:Element(name = "creator", required = false)
        var author: String? = "",

        @field:Element(name = "description")
        @param:Element(name = "description")
        var description: String? = null,

        @field:Element(name = "encoded", required = false)
        @param:Element(name = "encoded", required = false)
        var content: String? = null
    )

    @Root(name = "link", strict = false)
    data class Link @JvmOverloads constructor(
        @field:Text(required = false)
        @param:Text(required = false)
        var url: String? = null,

        @field:Attribute(required = false, name = "href")
        @param:Attribute(required = false, name = "href")
        var href: String? = null,

        @field:Attribute(required = false, name = "rel")
        @param:Attribute(required = false, name = "rel")
        var rel: String? = null,

        @field:Attribute(required = false, name = "type")
        @param:Attribute(required = false, name = "type")
        var type: String? = null
    )
}