package com.weilok.rssocto.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.weilok.rssocto.data.local.entities.Entry
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EntryContentViewModel @Inject constructor(
    private val state: SavedStateHandle
) : ViewModel() {
    val entry = state.get<Entry>("entry")
    val entryContent = entry?.content
}