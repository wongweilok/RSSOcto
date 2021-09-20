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

import androidx.lifecycle.LiveData
import androidx.room.*
import com.weilok.rssocto.data.local.entities.Feed
import com.weilok.rssocto.data.local.entities.FeedWithEntry

@Dao
interface FeedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeed(feed: Feed)

    @Update
    suspend fun updateFeed(feed: Feed)

    @Delete
    suspend fun deleteFeed(feed: Feed)

    @Query("DELETE FROM feed_table")
    suspend fun deteleAllFeed()

    @Query("SELECT * FROM feed_table WHERE feed_url = :id")
    suspend fun getFeedWithEntry(id: String): List<FeedWithEntry>

    @Query("SELECT * FROM feed_table")
    fun getAllFeed(): LiveData<List<Feed>>
}