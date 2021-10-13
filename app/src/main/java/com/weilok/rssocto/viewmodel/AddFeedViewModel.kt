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

package com.weilok.rssocto.viewmodel

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
import java.util.*
import javax.inject.Inject

import com.weilok.rssocto.data.AppRepository
import com.weilok.rssocto.ui.ADD_FEED_RESULT_OK

@HiltViewModel
class AddFeedViewModel @Inject constructor(
    private val repo: AppRepository
) : ViewModel(), Observable {
    val feeds = repo.localFeeds

    @Bindable
    val inputUrl = MutableLiveData<String>()

    @Bindable
    val urlValidation = MutableLiveData<String>()

    @Bindable
    val feedType = MutableLiveData<String>()

    // Get user input Feed URL and request for XML data
    fun getFeed() {
        val url = inputUrl.value!!

        when (feedType.value!!) {
            "RSS" -> fetchRssFeed(url)
            "ATOM" -> fetchAtomFeed(url)
        }

        inputUrl.value = ""
    }

    private fun fetchAtomFeed(url: String) {
        viewModelScope.launch {
            repo.fetchAtomFeed(url)
            addFeedEventChannel.send(AddFeedEvent.AddAndNavigateBack(ADD_FEED_RESULT_OK))
        }
    }

    private fun fetchRssFeed(url: String) {
        viewModelScope.launch {
            repo.fetchRssFeed(url)
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
                    repo.fetchFeedType(
                        inputUrl.value!!,
                        client,
                        urlValidation,
                        feedType,
                        isUrlValid,
                        isFeedExist
                    )
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