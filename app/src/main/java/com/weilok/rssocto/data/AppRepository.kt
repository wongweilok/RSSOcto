package com.weilok.rssocto.data

import androidx.lifecycle.MutableLiveData
import com.weilok.rssocto.data.remote.AtomFeed
import com.weilok.rssocto.services.Fetcher

class AppRepository {
    // Remote data
    val atomFeedLiveData: MutableLiveData<AtomFeed> = MutableLiveData()

    suspend fun fetchAtomFeed(url: String) {
        val response = Fetcher
            .getInstance()
            .getAtomFeed(url)

        atomFeedLiveData.postValue(response)
    }
}