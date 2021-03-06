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

package com.weilok.rssocto.data.local.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "feed_table",
    indices = [(Index(value = ["feed_url"], unique = true))]
)
data class Feed(
    @PrimaryKey
    @ColumnInfo(name = "feed_url")
    val url: String,
    @ColumnInfo(name = "feed_source_url")
    val sourceUrl: String,
    @ColumnInfo(name = "feed_image_url")
    val imageUrl: String,
    @ColumnInfo(name = "feed_title")
    val title: String,
    @ColumnInfo(name = "feed_type")
    val feedType: String
) : Parcelable