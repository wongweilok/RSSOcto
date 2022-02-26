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

package com.weilok.rssocto.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow

import com.weilok.rssocto.data.PreferenceHandler
import com.weilok.rssocto.data.local.entities.Entry
import com.weilok.rssocto.data.local.entities.Feed
import com.weilok.rssocto.data.repositories.EntryRepository
import com.weilok.rssocto.services.Refresher
import com.weilok.rssocto.utilities.TYPE_ATOM
import com.weilok.rssocto.utilities.TYPE_RSS

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val entryRepo: EntryRepository,
    private val refresher: Refresher,
    prefHandler: PreferenceHandler,
    state: SavedStateHandle
) : ViewModel() {
    val feed = state.get<Feed>("feed")
    val feedType = feed?.feedType
    private val feedId = feed?.url

    private val entryFilterPref = prefHandler.getFilterPref()

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
            TYPE_RSS -> fetchRssFeed(feedId!!)
            TYPE_ATOM -> fetchAtomFeed(feedId!!)
        }
    }

    private fun fetchAtomFeed(url: String) {
        viewModelScope.launch {
            refresher.refreshAtomFeed(url)

            entryEventChannel.send(EntryEvent.ShowRefreshMessage("Refreshing feed entries..."))
        }
    }

    private fun fetchRssFeed(url: String) {
        viewModelScope.launch {
            refresher.refreshRssFeed(url)

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