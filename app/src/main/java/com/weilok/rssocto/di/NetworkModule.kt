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

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

import com.weilok.rssocto.data.repositories.EntryRepository
import com.weilok.rssocto.data.repositories.FeedRepository
import com.weilok.rssocto.services.Fetcher
import com.weilok.rssocto.services.Refresher

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideFetcher(): Fetcher {
        return Fetcher.getInstance()
    }

    @Provides
    fun provideRefresher(
        feedRepo: FeedRepository,
        entryRepo: EntryRepository
    ): Refresher {
        return Refresher(feedRepo, entryRepo)
    }
}