/*
    Copyright (C) 2021-2022 Wong Wei Lok <wongweilok@disroot.org>

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
    @Root(name = "entry", strict = false)
    data class AtomEntry @JvmOverloads constructor(
        @field:Element(name = "id")
        @param:Element(name = "id")
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