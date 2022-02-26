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

package com.weilok.rssocto.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

import com.weilok.rssocto.data.repositories.FeedRepository
import com.weilok.rssocto.data.AppDatabase
import com.weilok.rssocto.data.local.dao.EntryDao
import com.weilok.rssocto.data.local.dao.FeedDao
import com.weilok.rssocto.data.repositories.EntryRepository
import com.weilok.rssocto.services.Fetcher

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideFeedDao(db: AppDatabase): FeedDao {
        return db.feedDao()
    }

    @Provides
    fun provideEntryDao(db: AppDatabase): EntryDao {
        return db.entryDao()
    }

    @Provides
    fun provideFeedRepository(
        feedDao: FeedDao, fetcher: Fetcher
    ): FeedRepository {
        return FeedRepository(feedDao, fetcher)
    }

    @Provides
    fun provideEntryRepository(
        entryDao: EntryDao
    ): EntryRepository {
        return EntryRepository(entryDao)
    }
}