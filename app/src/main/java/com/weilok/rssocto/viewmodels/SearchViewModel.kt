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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

import com.weilok.rssocto.data.local.entities.Entry
import com.weilok.rssocto.data.repositories.EntryRepository

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val entryRepo: EntryRepository
) : ViewModel() {
    val searchQuery = MutableStateFlow("")

    private val entriesFlow = searchQuery.flatMapLatest { query ->
        entryRepo.getEntriesWithQuery(query)
    }

    val entries = entriesFlow.asLiveData()

    fun onEntryClicked(entry: Entry) {
        viewModelScope.launch {
            searchEventChannel.send(SearchEvent.NavigateToContentView(entry))
        }
    }

    // Channel & Events
    private val searchEventChannel = Channel<SearchEvent>()
    val searchEvent = searchEventChannel.receiveAsFlow()

    sealed class SearchEvent {
        data class NavigateToContentView(val entry: Entry) : SearchEvent()
    }
}