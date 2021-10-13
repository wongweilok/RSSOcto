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
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

import com.weilok.rssocto.R
import com.weilok.rssocto.databinding.FragmentAddFeedBinding
import com.weilok.rssocto.viewmodel.FeedViewModel

@AndroidEntryPoint
class AddFeedFragment : Fragment(R.layout.fragment_add_feed) {
    private lateinit var binding: FragmentAddFeedBinding

    private val feedViewModel: FeedViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize binding for current view
        binding = FragmentAddFeedBinding.bind(view)

        binding.feedVM = feedViewModel
        binding.lifecycleOwner = activity

        initButtons()
        initFeedObserver()
    }

    private fun initButtons() {
        binding.btnAdd.setOnClickListener {
            feedViewModel.getFeed()

            runBlocking {
                launch {
                    delay(1000L)
                    val action = AddFeedFragmentDirections.actionAddFeedFragmentToFeedFragment()
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun initFeedObserver() {
        // Observe fetched data from Logcat for now
        feedViewModel.remoteAtomFeed.observe(viewLifecycleOwner) {
            Log.i("AtomFeed", it.toString())
        }

        feedViewModel.remoteRssFeed.observe(viewLifecycleOwner) {
            Log.i("RssFeed", it.toString())
        }

        feedViewModel.feeds.observe(viewLifecycleOwner) {
            Log.i("LocalFeed", it.toString())
        }

        feedViewModel.entries.observe(viewLifecycleOwner) {
            Log.i("LocalEntry", it.toString())
        }

        feedViewModel.feedType.observe(viewLifecycleOwner) {
            Log.i("UrlValid", it.toString())
        }
    }
}