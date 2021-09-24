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

package com.weilok.rssocto.services

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

class Parser {
    fun getFeedType(parser: XmlPullParser): String? {
        var eventType = parser.eventType
        var feedType: String? = null

        try {
            // Loop whole XML content
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tag: String

                when (eventType) {
                    XmlPullParser.END_TAG -> {
                        tag = parser.name

                        // Identify feed type based on root tag
                        if (tag.equals("rss", ignoreCase = true)) {
                            feedType = "RSS"
                        } else if (tag.equals("feed", ignoreCase = true)) {
                            feedType = "ATOM"
                        }
                    }
                }

                eventType = parser.next()
            }
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        }

        return feedType
    }
}