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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

import com.weilok.rssocto.data.local.entities.Feed
import com.weilok.rssocto.data.repositories.EntryRepository
import com.weilok.rssocto.data.repositories.FeedRepository
import com.weilok.rssocto.ui.ADD_FEED_RESULT_OK

// Messages to show when feeds changed
private const val FEED_ADDED_MSG = "Feed Added Successfully."
private const val FEED_DELETED_MSG = "Feed Deleted Successfully."
private const val ERROR_MSG = "Adding feed failed."

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedRepo: FeedRepository,
    private val entryRepo: EntryRepository
) : ViewModel() {
    val feeds = feedRepo.localFeeds

    fun onFeedClicked(feed: Feed) {
        viewModelScope.launch {
            feedEventChannel.send(FeedEvent.NavigateToEntryFragment(feed))
        }
    }

    fun deleteFeed(feed: Feed) {
        viewModelScope.launch {
            feedRepo.deleteFeed(feed)

            feedEventChannel.send(FeedEvent.ShowFeedChangedMessage(FEED_DELETED_MSG))
        }
    }

    fun markAllEntriesAsRead(feedId: String) {
        viewModelScope.launch {
            entryRepo.markAllEntriesAsRead(feedId)
        }
    }

    fun markAllEntriesAsUnread(feedId: String) {
        viewModelScope.launch {
            entryRepo.markAllEntriesAsUnread(feedId)
        }
    }

    fun onAddFeedResult(result: Int) {
        when (result) {
            ADD_FEED_RESULT_OK -> {
                viewModelScope.launch {
                    feedEventChannel.apply {
                        send(FeedEvent.ShowFeedChangedMessage(FEED_ADDED_MSG))
                        send(FeedEvent.ShowRecyclerView)
                    }
                }
            }
            else -> {
                viewModelScope.launch {
                    feedEventChannel.apply {
                        send(FeedEvent.ShowFeedChangedMessage(ERROR_MSG))
                        send(FeedEvent.ShowRecyclerView)
                    }
                }
            }
        }
    }

    // Channel & Events
    private val feedEventChannel = Channel<FeedEvent>()
    val feedEvent = feedEventChannel.receiveAsFlow()

    sealed class FeedEvent {
        data class ShowFeedChangedMessage(val message: String) : FeedEvent()
        data class NavigateToEntryFragment(val feed: Feed) : FeedEvent()

        object ShowRecyclerView : FeedEvent()
    }
}