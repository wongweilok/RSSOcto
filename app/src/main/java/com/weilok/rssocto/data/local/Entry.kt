package com.weilok.rssocto.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

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
    val date: String,
    @ColumnInfo(name = "entry_author")
    val author: String,
    @ColumnInfo(name = "entry_content")
    val content: String,
    @ColumnInfo(name = "read_status")
    val read: Boolean,
    @ColumnInfo(name = "feed_id")
    val feedId: String
)