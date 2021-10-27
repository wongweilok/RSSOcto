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

import com.weilok.rssocto.data.local.dao.EntryDao
import com.weilok.rssocto.data.local.dao.FeedDao
import com.weilok.rssocto.data.local.entities.Entry
import com.weilok.rssocto.data.local.entities.FeedWithEntry

class EntryRepository @Inject constructor(
    private val feedDao: FeedDao,
    private val entryDao: EntryDao
) {
    val localEntries = entryDao.getAllEntry().asLiveData()

    // Entry
    suspend fun insertEntry(entry: Entry) {
        entryDao.insertEntry(entry)
    }

    // Feed With Entries
    suspend fun getFeedWithEntries(id: String) : FeedWithEntry {
        return feedDao.getFeedWithEntry(id)
    }

    suspend fun getEntriesWithFeedId(id: String) : List<Entry> {
        return entryDao.getEntryWithFeedId(id)
    }
}