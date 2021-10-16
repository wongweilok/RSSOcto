package com.weilok.rssocto.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import com.weilok.rssocto.data.local.entities.Feed
import com.weilok.rssocto.data.repositories.EntryRepository

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val entryRepo: EntryRepository,
    private val state: SavedStateHandle
) : ViewModel() {
    val entries = entryRepo.localEntries

    val feed = state.get<Feed>("feed")
}