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

package com.weilok.rssocto.data

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.weilok.rssocto.data.local.dao.EntryDao
import com.weilok.rssocto.data.local.dao.FeedDao
import com.weilok.rssocto.data.local.entities.Entry
import com.weilok.rssocto.data.local.entities.Feed
import com.weilok.rssocto.data.remote.AtomFeed
import com.weilok.rssocto.data.remote.RssFeed
import com.weilok.rssocto.services.Fetcher
import com.weilok.rssocto.services.Validator
import okhttp3.OkHttpClient

class AppRepository(
    private val feedDao: FeedDao,
    private val entryDao: EntryDao
) {
    // Local data
    val localFeeds = feedDao.getAllFeed()
    val localEntries = entryDao.getAllEntry()

    // Feed
    suspend fun insertFeed(feed: Feed) {
        feedDao.insertFeed(feed)
    }

    // Entry
    suspend fun insertEntry(entry: Entry) {
        entryDao.insertEntry(entry)
    }

    // Remote data
    val atomFeedLiveData: MutableLiveData<AtomFeed> = MutableLiveData()
    val rssFeedLiveData: MutableLiveData<RssFeed> = MutableLiveData()

    suspend fun fetchAtomFeed(url: String) {
        // Fetch Atom Feed from web
        val response = Fetcher
            .getInstance()
            .getAtomFeed(url)

        val entryList: List<AtomFeed.AtomEntry> = response.entryList!!

        // Add Feed and Entry data into local database
        insertFeed(Feed(response.url!!, response.title!!))
        for (i in entryList.indices) {
            insertEntry(
                Entry(
                    entryList[i].url!!,
                    entryList[i].title!!,
                    entryList[i].date!!,
                    entryList[i].author!!,
                    entryList[i].content!!,
                    false,
                    response.url!!
                )
            )
        }

        atomFeedLiveData.postValue(response)
    }

    suspend fun fetchRssFeed(url: String) {
        // Fetch RSS Feed from web
        val response = Fetcher
            .getInstance()
            .getRssFeed(url)

        val entryList: List<RssFeed.RssEntry> = response.entryList!!

        // Add Feed and Entry data into local database
        insertFeed(Feed(response.urlList?.get(0)!!.url!!, response.title!!))
        for (i in entryList.indices) {
            insertEntry(
                Entry(
                    entryList[i].url!!,
                    entryList[i].title!!,
                    entryList[i].date!!,
                    entryList[i].author!!,
                    entryList[i].content!!,
                    false,
                    response.urlList?.get(0)!!.url!!
                )
            )
        }

        rssFeedLiveData.postValue(response)
    }

    // Validate URL and get feed type
    fun fetchFeedType(
        url: String,
        client: OkHttpClient,
        urlValid: MutableLiveData<String>,
        feedType: MutableLiveData<String>,
        isUrlValid: ObservableBoolean
    ) {
        Validator().validate(
            url,
            client,
            urlValid,
            feedType,
            isUrlValid
        )
    }
}