/*
    Copyright (C) 2021-2022 Wong Wei Lok <wongweilok@disroot.org>

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

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

import com.weilok.rssocto.data.local.dao.EntryDao
import com.weilok.rssocto.data.local.entities.Entry
import com.weilok.rssocto.data.EntriesView
import com.weilok.rssocto.data.local.entities.EntryWithFeed

class EntryRepository @Inject constructor(
    private val entryDao: EntryDao
) {
    // Entry
    suspend fun insertEntry(entry: Entry) {
        entryDao.insertEntry(entry)
    }

    suspend fun markEntryAsRead(id: String) {
        entryDao.markEntryAsRead(id)
    }

    suspend fun markEntryAsUnread(id: String) {
        entryDao.markEntryAsUnread(id)
    }

    suspend fun markAllEntriesAsRead(feedId: String) {
        entryDao.markAllEntriesAsRead(feedId)
    }

    suspend fun markAllEntriesAsUnread(feedId: String) {
        entryDao.markAllEntriesAsUnread(feedId)
    }

    suspend fun favEntry(id: String) {
        entryDao.favEntry(id)
    }

    suspend fun unfavEntry(id: String) {
        entryDao.unfavEntry(id)
    }

    suspend fun checkEntryExist(id: String): Boolean {
        return entryDao.checkEntryExist(id)
    }

    // Entries with feed ID
    fun getEntries(
        id: String,
        entriesView: EntriesView
    ): Flow<List<EntryWithFeed>> {
        return entryDao.getEntries(id, entriesView)
    }

    // Get entries with search query
    fun getEntriesWithQuery(query: String): Flow<List<EntryWithFeed>> {
        return entryDao.getEntriesWithQuery(query)
    }
}