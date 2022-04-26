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

package com.weilok.rssocto.ui

import android.content.Intent
import android.content.res.TypedArray
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint

import com.weilok.rssocto.databinding.ActivityEntryContentBinding
import com.weilok.rssocto.R
import com.weilok.rssocto.viewmodels.EntryContentViewModel

@AndroidEntryPoint
class EntryContentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEntryContentBinding

    private val viewModel: EntryContentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntryContentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Content"

        // Html settings and CSS
        val cssBackgroundColor = getColorAttrVal(R.attr.cssBackground)
        val cssTextColor = getColorAttrVal(R.attr.cssTextColor)
        val cssLinkColor = getColorAttrVal(R.attr.cssLinkColor)
        val cssCodeBgColor = getColorAttrVal(R.attr.cssCodeBgColor)

        val css = "<head>" +
                "<style type='text/css'>" +
                "* {word-break: break-word; max-width: 100%;}" +
                "body {background: $cssBackgroundColor; color: $cssTextColor;}" +
                "a {color: $cssLinkColor;}" +
                "pre {white-space: pre-wrap; background: $cssCodeBgColor;}" +
                "code {overflow-wrap: break-word;}" +
                "figure {width: auto !important;}" +
                "img {height: auto !important;}" +
                "</style>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "</head>"
        val bodyStart = "<body>"
        val bodyEnd = "</body>"
        val horizontalRule = "<hr>"

        // Mark this entry as read
        viewModel.markEntryAsRead(viewModel.entryId!!)

        // Enable up button
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        // Entry details use for displaying above content
        val entry = viewModel.entry!!
        val entryTitle = "<h2>${entry.title}</h2><br>"
        val entryDateWithAuthor = StringBuilder(entry.formattedDate)

        if (entry.author.isNotEmpty()) {
            entryDateWithAuthor.apply {
                append("<br>Author - ")
                append(entry.author)
            }
        }

        // Combine into one string
        val html = StringBuilder(css)
            .append(bodyStart)
            .append(entryTitle)
            .append(entryDateWithAuthor)
            .append(horizontalRule)
            .append(viewModel.entryContent!!)
            .append(bodyEnd)
            .toString()

        // Display feed entry content with WebView
        binding.wvEntryContent.apply {
            isHorizontalScrollBarEnabled = false
            loadDataWithBaseURL(
                "",
                html,
                "text/html",
                null,
                null
            )
        }

        binding.fabShare.setOnClickListener {
            val intent = Intent()
            intent.apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, viewModel.entryId)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(intent, "Share via"))
        }

        var isFavourite = viewModel.entryFavStat!!
        var isRead = true

        binding.bottomAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.optActionFavorite -> {
                    isFavourite = if (!isFavourite) {
                        item.setIcon(R.drawable.ic_favorite)
                        item.title = "Unfavorite"

                        viewModel.favEntry(viewModel.entryId!!)
                        true
                    } else {
                        item.setIcon(R.drawable.ic_favorite_border)
                        item.title = "Favorite"

                        viewModel.unfavEntry(viewModel.entryId!!)
                        false
                    }

                    true
                }
                R.id.optActionMarkAsUnread -> {
                    isRead = if (!isRead) {
                        item.setIcon(R.drawable.ic_unread)
                        item.title = "Mark as unread"

                        viewModel.markEntryAsRead(viewModel.entryId!!)
                        true
                    } else {
                        item.setIcon(R.drawable.ic_read)
                        item.title = "Mark as read"

                        viewModel.markEntryAsUnread(viewModel.entryId!!)
                        false
                    }

                    true
                }
                R.id.optActionOpenInBrowser -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(viewModel.entryId))
                    startActivity(intent)

                    true
                }
                else -> false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // Get color value from XML resources
    private fun getColorAttrVal(attr: Int): String {
        val a = intArrayOf(attr)
        val at: TypedArray = this.theme.obtainStyledAttributes(TypedValue().data, a)
        val testColor = at.getColor(0, 0)

        return String.format("#%X", testColor)
            .replaceFirst("FF", "", false)
    }
}