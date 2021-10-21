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

package com.weilok.rssocto.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

import com.weilok.rssocto.R
import com.weilok.rssocto.databinding.FragmentEntryContentBinding
import com.weilok.rssocto.viewmodels.EntryContentViewModel

class EntryContentFragment : Fragment(R.layout.fragment_entry_content) {
    private lateinit var binding: FragmentEntryContentBinding

    private val viewModel: EntryContentViewModel by viewModels()

    // Html settings and CSS
    private val css = "<head>" +
            "<style type='text/css'>" +
            "* {word-break: break-word; max-width: 100%;}" +
            "pre {white-space: pre-wrap;}" +
            "figure {width: auto !important;}" +
            "img {height: auto !important;}" +
            "</style>" +
            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
            "</head>"
    private val bodyStart = "<body>"
    private val bodyEnd = "</body>"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize binding for current view
        binding = FragmentEntryContentBinding.bind(view)

        // Combine into one string
        val html = StringBuilder(css)
            .append(bodyStart)
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
    }
}