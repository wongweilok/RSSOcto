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

package com.weilok.rssocto

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.weilok.rssocto.data.AppRepository
import com.weilok.rssocto.databinding.FragmentAddFeedBinding
import com.weilok.rssocto.viewmodel.FeedViewModel
import com.weilok.rssocto.viewmodel.FeedViewModelFactory

class AddFeedFragment : Fragment(R.layout.fragment_add_feed) {
    private lateinit var binding: FragmentAddFeedBinding
    private lateinit var feedViewModel: FeedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize binding for current view
        binding = FragmentAddFeedBinding.bind(view)

        val repo = AppRepository()
        val factory = FeedViewModelFactory(repo)

        // Initialize FeedViewModel
        feedViewModel = ViewModelProvider(this, factory)
            .get(FeedViewModel::class.java)

        binding.feedVM = feedViewModel

        initFeedObserver()
    }

    private fun initFeedObserver() {
        // Observe fetched data from Logcat for now
        feedViewModel.remoteAtomFeed.observe(viewLifecycleOwner) {
            Log.i("RemoteFeed", it.toString())
        }
    }
}