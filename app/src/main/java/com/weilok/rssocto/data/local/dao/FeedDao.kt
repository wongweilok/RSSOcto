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

import com.weilok.rssocto.data.local.entities.Feed

@Dao
interface FeedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeed(feed: Feed)

    @Update
    suspend fun updateFeed(feed: Feed)

    @Delete
    suspend fun deleteFeed(feed: Feed)

    @Query("DELETE FROM feed_table")
    suspend fun deleteAllFeed()

    @Query("SELECT EXISTS(SELECT * FROM feed_table WHERE feed_url = :id)")
    suspend fun checkFeedExist(id: String): Boolean

    @Query("SELECT * FROM feed_table WHERE feed_type = 'ATOM'")
    suspend fun getAllAtomFeeds(): List<Feed>

    @Query("SELECT * FROM feed_table WHERE feed_type = 'RSS'")
    suspend fun getAllRssFeeds(): List<Feed>

    @Query("SELECT * FROM feed_table ORDER BY feed_title ASC")
    fun getAllFeed(): Flow<List<Feed>>
}