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

package com.weilok.rssocto.data.local.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
@Entity(
    tableName = "entry_table",
    foreignKeys = [ForeignKey(
        entity = Feed::class,
        parentColumns = ["feed_url"],
        childColumns = ["feed_id"],
        onDelete = CASCADE
    )]
)
data class Entry(
    @PrimaryKey
    @ColumnInfo(name = "entry_url")
    val url: String,
    @ColumnInfo(name = "entry_title")
    val title: String,
    @ColumnInfo(name = "entry_pub_date")
    val date: Date,
    @ColumnInfo(name = "entry_author")
    val author: String,
    @ColumnInfo(name = "entry_content")
    val content: String,
    @ColumnInfo(name = "read_status")
    val read: Boolean,
    @ColumnInfo(name = "feed_id")
    val feedId: String
) : Parcelable {
    val formattedDate: String
        get() = SimpleDateFormat("EEE, dd MMM yyyy hh:mm aaa", Locale.ENGLISH)
            .format(date)
            .toString()
}