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

package com.weilok.rssocto.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow

import com.weilok.rssocto.data.local.entities.Entry
import com.weilok.rssocto.data.EntriesView
import com.weilok.rssocto.data.local.entities.EntryWithFeed

@Dao
interface EntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: Entry)

    @Update
    suspend fun updateEntry(entry: Entry)

    @Delete
    suspend fun deleteEntry(entry: Entry)

    @Query("DELETE FROM entry_table")
    suspend fun deleteAllEntry()

    @Query("UPDATE entry_table SET read_status = 1 WHERE entry_url = :id")
    suspend fun markEntryAsRead(id: String)

    @Query("UPDATE entry_table SET read_status = 0 WHERE entry_url = :id")
    suspend fun markEntryAsUnread(id: String)

    @Query("UPDATE entry_table SET read_status = 1 WHERE feed_id = :feedId")
    suspend fun markAllEntriesAsRead(feedId: String)

    @Query("UPDATE entry_table SET read_status = 0 WHERE feed_id = :feedId")
    suspend fun markAllEntriesAsUnread(feedId: String)

    @Query("UPDATE entry_table SET favorite_status = 1 WHERE entry_url = :id")
    suspend fun favEntry(id: String)

    @Query("UPDATE entry_table SET favorite_status = 0 WHERE entry_url = :id")
    suspend fun unfavEntry(id: String)

    @Query("SELECT EXISTS(SELECT * FROM entry_table WHERE entry_url = :id)")
    suspend fun checkEntryExist(id: String): Boolean

    fun getEntries(feedId: String, entriesView: EntriesView): Flow<List<EntryWithFeed>> {
        return when (entriesView) {
            EntriesView.BY_ALL -> getAllEntries(feedId)
            EntriesView.BY_UNREAD -> getUnreadEntries(feedId)
        }
    }

    @Query("SELECT * FROM entry_table INNER JOIN feed_table ON feed_url = feed_id WHERE feed_id = :feedId ORDER BY entry_pub_date DESC")
    fun getAllEntries(feedId: String): Flow<List<EntryWithFeed>>

    @Query("SELECT * FROM entry_table INNER JOIN feed_table ON feed_url = feed_id WHERE feed_id = :feedId AND read_status = 0 ORDER BY entry_pub_date DESC")
    fun getUnreadEntries(feedId: String): Flow<List<EntryWithFeed>>

    @Query("SELECT * FROM entry_table INNER JOIN feed_table ON feed_url = feed_id WHERE entry_title LIKE '%' || :searchQuery || '%' ORDER BY entry_pub_date DESC")
    fun getEntriesWithQuery(searchQuery: String): Flow<List<EntryWithFeed>>

    @Query("SELECT * FROM entry_table ORDER BY entry_pub_date")
    fun getAllEntry(): Flow<List<Entry>>

    @Query("SELECT * FROM entry_table INNER JOIN feed_table ON feed_url = feed_id WHERE feed_id = :feedId")
    fun getEntriesWithFeed(feedId: String): Flow<List<EntryWithFeed>>
}