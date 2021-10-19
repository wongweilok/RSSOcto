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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

import com.weilok.rssocto.data.local.entities.Entry
import com.weilok.rssocto.data.local.entities.Feed
import com.weilok.rssocto.data.repositories.EntryRepository

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val entryRepo: EntryRepository,
    private val state: SavedStateHandle
) : ViewModel() {
    val feed = state.get<Feed>("feed")
    val feedId = feed?.url
    val feedTitle = feed?.title

    fun onEntryClicked(entry: Entry) {
        viewModelScope.launch {
            entryEventChannel.send(EntryEvent.NavigateToContentFragment(entry))
        }
    }

    // Feed with entries
    fun getFeedWithEntries(id: String) {
        viewModelScope.launch {
            val response = entryRepo.getFeedWithEntries(id)
            entryEventChannel.send(EntryEvent.ListOfEntries(response.entries))
        }
    }

    // Channel & Events
    private val entryEventChannel = Channel<EntryEvent>()
    val entryEvent = entryEventChannel.receiveAsFlow()

    sealed class EntryEvent {
        data class ListOfEntries(val list: List<Entry>) : EntryEvent()
        data class NavigateToContentFragment(val entry: Entry) : EntryEvent()
    }
}