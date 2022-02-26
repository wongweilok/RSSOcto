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

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

import com.weilok.rssocto.data.local.entities.Entry
import com.weilok.rssocto.data.remote.AtomFeed
import com.weilok.rssocto.data.remote.RssFeed
import com.weilok.rssocto.data.repositories.EntryRepository
import com.weilok.rssocto.data.repositories.FeedRepository
import com.weilok.rssocto.utilities.ATOM_DATE_FMT
import com.weilok.rssocto.utilities.RSS_DATE_FMT

class Refresher @Inject constructor(
    private val feedRepo: FeedRepository,
    private val entryRepo: EntryRepository
) {
    suspend fun refreshFeeds() {
        val atomFeedList = feedRepo.getAllAtomFeed()
        val rssFeedList = feedRepo.getAllRssFeed()

        for (i in atomFeedList) {
            Log.i("atomTag", i.url)
            refreshAtomFeed(i.url)
        }

        for (i in rssFeedList) {
            Log.i("rssTag", i.url)
            refreshRssFeed(i.url)
        }
    }

    suspend fun refreshAtomFeed(url: String) {
        // Fetch Atom Feed from web
        val response = feedRepo.fetchAtomFeed(url)
        val entryList: List<AtomFeed.AtomEntry> = response.entryList!!

        // Date format
        val dtFormatter = SimpleDateFormat(ATOM_DATE_FMT, Locale.ENGLISH)

        // Regex pattern for image URL
        val imgPattern = "(https|http)\\S+\\.(jpg|JPG|png|PNG|jpeg|JPEG|gif|GIF)".toRegex()

        // Add Entry data into local database
        for (i in entryList.indices) {
            /*
             * Trim trailing white spaces from string date.
             * Remove milli-seconds from string date using Regex.
             * Parse date into date type.
             */
            val date = entryList[i].date!!
                .trim()
                .replace(Regex("\\.[0-9]+"), "")
            val parsedDate: Date? = dtFormatter.parse(date)

            if (!entryRepo.checkEntryExist(entryList[i].url!!)) {
                // Get image URL from entry content with regex
                val content = entryList[i].content!!
                val result = imgPattern.find(content, 0)
                val imgUrl = result?.value ?: ""

                entryRepo.insertEntry(
                    Entry(
                        entryList[i].url!!,
                        imgUrl,
                        entryList[i].title!!,
                        parsedDate!!,
                        entryList[i].author!!,
                        entryList[i].content!!,
                        false,
                        url
                    )
                )
            }
        }
    }

    suspend fun refreshRssFeed(url: String) {
        // Fetch RSS Feed from web
        val response = feedRepo.fetchRssFeed(url)
        val entryList: List<RssFeed.RssEntry> = response.entryList!!

        // Date format
        val dtFormatter = SimpleDateFormat(RSS_DATE_FMT, Locale.ENGLISH)

        // Regex pattern for image URL
        val imgPattern = "(https|http)\\S+\\.(jpg|JPG|png|PNG|jpeg|JPEG|gif|GIF)".toRegex()

        // Add Entry data into local database
        for (i in entryList.indices) {
            /*
             * Trim trailing white spaces from string date
             * before parsing to date type
             */
            val date = entryList[i].date!!.trim()
            val parsedDate: Date? = dtFormatter.parse(date)

            if (!entryRepo.checkEntryExist(entryList[i].url!!)) {
                var content = entryList[i].description!!
                if (entryList[i].content != null) {
                    content = entryList[i].content!!
                }

                // Get image URL from entry content with regex
                val result = imgPattern.find(content, 0)
                val imgUrl = result?.value ?: ""

                entryRepo.insertEntry(
                    Entry(
                        entryList[i].url!!,
                        imgUrl,
                        entryList[i].title!!,
                        parsedDate!!,
                        entryList[i].author!!,
                        content,
                        false,
                        url
                    )
                )
            }
        }
    }
}