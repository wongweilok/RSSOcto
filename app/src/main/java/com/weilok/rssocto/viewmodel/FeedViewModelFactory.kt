package com.weilok.rssocto.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.weilok.rssocto.data.AppRepository
import java.lang.IllegalArgumentException

class FeedViewModelFactory(private val repo: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            return FeedViewModel(repo) as T
        }
        throw  IllegalArgumentException("Unknown ViewModel Class.")
    }
}