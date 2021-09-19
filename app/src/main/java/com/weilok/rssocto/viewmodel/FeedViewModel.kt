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

    @Bindable
    val inputUrl = MutableLiveData<String>()

    fun getFeed() {
        val url = inputUrl.value!!

        fetchAtomFeed(url)

        inputUrl.value = ""
    }

    fun fetchAtomFeed(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.fetchAtomFeed(url)
        }
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }
}