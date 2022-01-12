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

package com.weilok.rssocto.viewmodels

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

import com.weilok.rssocto.data.repositories.FeedRepository
import com.weilok.rssocto.data.local.entities.Entry
import com.weilok.rssocto.data.local.entities.Feed
import com.weilok.rssocto.data.remote.AtomFeed
import com.weilok.rssocto.data.remote.RssFeed
import com.weilok.rssocto.data.repositories.EntryRepository
import com.weilok.rssocto.utilities.*

@HiltViewModel
class AddFeedViewModel @Inject constructor(
    private val feedRepo: FeedRepository,
    private val entryRepo: EntryRepository
) : ViewModel(), Observable {
    @Bindable
    val inputUrl = MutableLiveData<String>()
    @Bindable
    val feedName = MutableLiveData<String>()
    @Bindable
    val urlValidation = MutableLiveData<String>()
    @Bindable
    val feedType = MutableLiveData<String>()

    // Get user input Feed URL and request for XML data
    fun getFeed() {
        val url = inputUrl.value!!

        when (feedType.value!!) {
            TYPE_RSS -> fetchRssFeed(url)
            TYPE_ATOM -> fetchAtomFeed(url)
        }

        isFeedExist.set(true)
    }

    private fun fetchAtomFeed(url: String) {
        viewModelScope.launch {
            // Fetch Atom Feed from web
            val response = feedRepo.fetchAtomFeed(url)
            val entryList: List<AtomFeed.AtomEntry> = response.entryList!!

            // Date format
            val dtFormatter = SimpleDateFormat(ATOM_DATE_FMT, Locale.ENGLISH)

            // Default or custom feed title
            val feedTitle = feedName.value ?: response.title

            // Add Feed and Entry data into local database
            feedRepo.insertFeed(
                Feed(
                    url,
                    response.url!!,
                    feedTitle!!,
                    feedType.value!!
                )
            )
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

                entryRepo.insertEntry(
                    Entry(
                        entryList[i].url!!,
                        entryList[i].title!!,
                        parsedDate!!,
                        entryList[i].author!!,
                        entryList[i].content!!,
                        false,
                        url
                    )
                )
            }

            addFeedEventChannel.send(AddFeedEvent.AddAndNavigateBack(ADD_FEED_RESULT_OK))
        }
    }

    private fun fetchRssFeed(url: String) {
        viewModelScope.launch {
            // Fetch RSS Feed from web
            val response = feedRepo.fetchRssFeed(url)
            val entryList: List<RssFeed.RssEntry> = response.entryList!!

            // Date format
            val dtFormatter = SimpleDateFormat(RSS_DATE_FMT, Locale.ENGLISH)

            // Get source URL from list
            val sourceUrl = response.urlList?.get(0)!!.url ?: response.urlList?.get(0)!!.href

            // Default or custom feed title
            val feedTitle = feedName.value ?: response.title

            // Add Feed and Entry data into local database
            feedRepo.insertFeed(
                Feed(
                    url,
                    sourceUrl!!,
                    feedTitle!!,
                    feedType.value!!
                )
            )
            for (i in entryList.indices) {
                /*
                 * Trim trailing white spaces from string date
                 * before parsing to date type
                 */
                val date = entryList[i].date!!.trim()
                val parsedDate: Date? = dtFormatter.parse(date)

                var content = entryList[i].description!!
                if (entryList[i].content != null) {
                    content = entryList[i].content!!
                }
                entryRepo.insertEntry(
                    Entry(
                        entryList[i].url!!,
                        entryList[i].title!!,
                        parsedDate!!,
                        entryList[i].author!!,
                        content,
                        false,
                        url
                    )
                )
            }

            addFeedEventChannel.send(AddFeedEvent.AddAndNavigateBack(ADD_FEED_RESULT_OK))
        }
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    // Fetch Feed format for URL validation
    private var timer = Timer()
    private val delay: Long = 1000
    private val client = OkHttpClient()

    val isUrlValid = ObservableBoolean(false)
    val isFeedExist = ObservableBoolean(false)

    fun onTextChanged() {
        timer.cancel()
        isUrlValid.set(false)
    }

    fun afterTextChanged() {
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                viewModelScope.launch(Dispatchers.IO) {
                    if (inputUrl.value != "") {
                        feedRepo.fetchFeedType(
                            inputUrl.value!!,
                            feedName,
                            client,
                            urlValidation,
                            feedType,
                            isUrlValid,
                            isFeedExist
                        )
                    } else {
                        urlValidation.postValue("")
                    }
                }
            }
        }, delay)
    }

    // Channel & Events
    private val addFeedEventChannel = Channel<AddFeedEvent>()
    val addFeedEvent = addFeedEventChannel.receiveAsFlow()

    sealed class AddFeedEvent {
        data class AddAndNavigateBack(val result: Int) : AddFeedEvent()
    }
}