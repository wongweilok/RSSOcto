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

package com.weilok.rssocto.data.repositories

import androidx.lifecycle.asLiveData
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

import com.weilok.rssocto.data.local.dao.EntryDao
import com.weilok.rssocto.data.local.entities.Entry
import com.weilok.rssocto.data.EntriesView

class EntryRepository @Inject constructor(
    private val entryDao: EntryDao
) {
    // Entry
    suspend fun insertEntry(entry: Entry) {
        entryDao.insertEntry(entry)
    }

    suspend fun markEntriesAsRead(id: String) {
        entryDao.markEntriesAsRead(id)
    }

    suspend fun markAllEntriesAsRead(feedId: String) {
        entryDao.markAllEntriesAsRead(feedId)
    }

    suspend fun checkEntryExist(id: String): Boolean {
        return entryDao.checkEntryExist(id)
    }

    // Entries with feed ID
    fun getEntries(
        id: String,
        entriesView: EntriesView
    ): Flow<List<Entry>> {
        return entryDao.getEntries(id, entriesView)
    }

    // Get entries with search query
    fun getEntriesWithQuery(query: String): Flow<List<Entry>> {
        return entryDao.getEntriesWithQuery(query)
    }
}