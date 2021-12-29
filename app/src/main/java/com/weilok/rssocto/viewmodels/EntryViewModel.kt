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

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

import com.weilok.rssocto.data.PrefHandler
import com.weilok.rssocto.data.local.entities.Entry
import com.weilok.rssocto.data.local.entities.Feed
import com.weilok.rssocto.data.remote.AtomFeed
import com.weilok.rssocto.data.remote.RssFeed
import com.weilok.rssocto.data.repositories.EntryRepository
import com.weilok.rssocto.data.repositories.FeedRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val entryRepo: EntryRepository,
    private val feedRepo: FeedRepository,
    @ApplicationContext context: Context,
    state: SavedStateHandle
) : ViewModel() {
    val feed = state.get<Feed>("feed")
    val feedType = feed?.feedType
    private val feedId = feed?.url

    private val entryFilterPref = PrefHandler(context).getEntryFilterPref()

    val entryFilter = MutableStateFlow(entryFilterPref)

    private val getEntriesWithFeedId = entryFilter.flatMapLatest { filter ->
        entryRepo.getEntries(feedId!!, filter)
    }

    val entries = getEntriesWithFeedId.asLiveData()

    fun onEntryClicked(entry: Entry) {
        viewModelScope.launch {
            entryEventChannel.send(EntryEvent.NavigateToContentView(entry))
        }
    }

    fun markEntryAs(entry: Entry) {
        viewModelScope.launch {
            if (entry.read) {
                entryRepo.markEntryAsUnread(entry.url)
            } else {
                entryRepo.markEntryAsRead(entry.url)
            }
        }
    }

    // Refresh feed entries fetching feed again
    fun refreshFeedEntries() {
        when (feedType) {
            "RSS" -> fetchRssFeed(feedId!!)
            "ATOM" -> fetchAtomFeed(feedId!!)
        }
    }

    private fun fetchAtomFeed(url: String) {
        viewModelScope.launch {
            // Fetch Atom Feed from web
            val response = feedRepo.fetchAtomFeed(url)
            val entryList: List<AtomFeed.AtomEntry> = response.entryList!!

            // Date format
            val dtFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH)

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
            }

            entryEventChannel.send(EntryEvent.ShowRefreshMessage("Refreshing feed entries..."))
        }
    }

    private fun fetchRssFeed(url: String) {
        viewModelScope.launch {
            // Fetch RSS Feed from web
            val response = feedRepo.fetchRssFeed(url)
            val entryList: List<RssFeed.RssEntry> = response.entryList!!

            // Date format
            val dtFormatter = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)

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
            }

            entryEventChannel.send(EntryEvent.ShowRefreshMessage("Refreshing feed entries..."))
        }
    }

    // Channel & Events
    private val entryEventChannel = Channel<EntryEvent>()
    val entryEvent = entryEventChannel.receiveAsFlow()

    sealed class EntryEvent {
        data class NavigateToContentView(val entry: Entry) : EntryEvent()
        data class ShowRefreshMessage(val message: String) : EntryEvent()
    }
}