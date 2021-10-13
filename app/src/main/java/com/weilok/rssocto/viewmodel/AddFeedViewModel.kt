package com.weilok.rssocto.viewmodel

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weilok.rssocto.data.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.util.*
import javax.inject.Inject

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
        viewModelScope.launch(Dispatchers.IO) {
            repo.fetchAtomFeed(url)
        }
    }

    private fun fetchRssFeed(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.fetchRssFeed(url)
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
}