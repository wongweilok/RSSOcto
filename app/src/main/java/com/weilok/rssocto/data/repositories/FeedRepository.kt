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

package com.weilok.rssocto.data.repositories

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import okhttp3.OkHttpClient
import javax.inject.Inject

import com.weilok.rssocto.data.local.dao.FeedDao
import com.weilok.rssocto.data.local.entities.Feed
import com.weilok.rssocto.data.remote.AtomFeed
import com.weilok.rssocto.data.remote.RssFeed
import com.weilok.rssocto.services.Fetcher
import com.weilok.rssocto.services.Validator

class FeedRepository @Inject constructor(
    private val feedDao: FeedDao,
    private val fetcher: Fetcher
) {
    // Local data
    val localFeeds = feedDao.getAllFeed().asLiveData()

    // Feed
    suspend fun insertFeed(feed: Feed) {
        feedDao.insertFeed(feed)
    }

    suspend fun deleteFeed(feed: Feed) {
        feedDao.deleteFeed(feed)
    }

    suspend fun checkFeedExist(id: String): Boolean {
        return feedDao.checkFeedExist(id)
    }

    // Get remote data
    suspend fun fetchAtomFeed(url: String) : AtomFeed {
        return fetcher.getAtomFeed(url)
    }

    suspend fun fetchRssFeed(url: String) : RssFeed {
        return fetcher.getRssFeed(url)
    }

    // Validate URL and get feed type
    suspend fun fetchFeedType(
        url: String,
        feedName: MutableLiveData<String>,
        client: OkHttpClient,
        urlValid: MutableLiveData<String>,
        feedType: MutableLiveData<String>,
        isUrlValid: ObservableBoolean,
        isFeedExist: ObservableBoolean
    ) {
        if (!checkFeedExist(url)) {
            isFeedExist.set(false)
            Validator().validate(
                url,
                feedName,
                client,
                urlValid,
                feedType,
                isUrlValid
            )
        } else {
            urlValid.postValue("")
            urlValid.postValue("Feed already exist.")
            feedType.postValue("")
            isFeedExist.set(true)
        }
    }
}