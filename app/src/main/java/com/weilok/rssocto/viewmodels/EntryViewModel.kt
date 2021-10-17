package com.weilok.rssocto.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weilok.rssocto.data.local.entities.Entry
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

import com.weilok.rssocto.data.local.entities.Feed
import com.weilok.rssocto.data.repositories.EntryRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val entryRepo: EntryRepository,
    private val state: SavedStateHandle
) : ViewModel() {
    val entries = entryRepo.localEntries

    val feed = state.get<Feed>("feed")
    val feedId = feed?.url
    val feedTitle = feed?.title

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
    }
}