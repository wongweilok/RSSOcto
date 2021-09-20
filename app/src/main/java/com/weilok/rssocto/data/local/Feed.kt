package com.weilok.rssocto.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feed_table")
data class Feed(
    @PrimaryKey
    @ColumnInfo(name = "feed_url")
    val url: String,
    @ColumnInfo(name = "feed_title")
    val title: String
)