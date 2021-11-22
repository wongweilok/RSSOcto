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

package com.weilok.rssocto.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow

import com.weilok.rssocto.data.local.entities.Entry

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

    @Query("SELECT * FROM entry_table WHERE feed_id = :feedId ORDER BY entry_pub_date DESC")
    suspend fun getEntryWithFeedId(feedId: String): List<Entry>

    @Query("SELECT * FROM entry_table WHERE feed_id = :feedId AND read_status = 0 ORDER BY entry_pub_date DESC")
    suspend fun getUnreadEntries(feedId: String): List<Entry>

    @Query("SELECT * FROM entry_table ORDER BY entry_pub_date")
    fun getAllEntry(): Flow<List<Entry>>
}