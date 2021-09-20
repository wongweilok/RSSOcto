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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weilok.rssocto.data.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FeedViewModel(private val repo: AppRepository) : ViewModel(), Observable {
    val remoteAtomFeed = repo.atomFeedLiveData
    val feeds = repo.localFeeds
    val entries = repo.localEntries

    @Bindable
    val inputUrl = MutableLiveData<String>()

    // Get user input Feed URL and request for XML data
    fun getFeed() {
        val url = inputUrl.value!!

        fetchAtomFeed(url)

        inputUrl.value = ""
    }

    private fun fetchAtomFeed(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.fetchAtomFeed(url)
        }
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }
}