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
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

import com.weilok.rssocto.R
import com.weilok.rssocto.adapter.FeedAdapter
import com.weilok.rssocto.data.local.entities.Feed
import com.weilok.rssocto.databinding.FragmentFeedBinding
import com.weilok.rssocto.viewmodels.FeedViewModel

@AndroidEntryPoint
class FeedFragment : Fragment(R.layout.fragment_feed), FeedAdapter.OnFeedItemClickListener {
    private lateinit var binding: FragmentFeedBinding

    private val feedViewModel: FeedViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize binding for current view
        binding = FragmentFeedBinding.bind(view)

        initButton()
        initRecyclerView()

        // Receive request from other fragment
        setFragmentResultListener("add_feed_request") { _, bundle ->
            val result = bundle.getInt("add_feed_result")
            feedViewModel.onAddFeedResult(result)
        }

        // Collect Signal from Event Channel
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            feedViewModel.feedEvent.collect { event ->
                when (event) {
                    is FeedViewModel.FeedEvent.ShowFeedAddedMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_SHORT).show()
                        if (binding.tvNoFeed.visibility == View.VISIBLE) {
                            binding.rvFeedList.visibility = View.VISIBLE
                            binding.tvNoFeed.visibility = View.GONE
                        }
                    }
                    is FeedViewModel.FeedEvent.NavigateToEntryFragment -> {
                        val action = FeedFragmentDirections
                            .actionFeedFragmentToEntryFragment(event.feed, event.feed.title)
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    private fun initButton() {
        binding.fabAddFeed.setOnClickListener {
            val action = FeedFragmentDirections.actionFeedFragmentToAddFeedFragment()
            findNavController().navigate(action)
        }
    }

    private fun initRecyclerView() {
        val feedAdapter = FeedAdapter(this)

        binding.rvFeedList.adapter = feedAdapter
        binding.rvFeedList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFeedList.setHasFixedSize(true)

        feedViewModel.feeds.observe(viewLifecycleOwner) { list ->
            // Display different layout when data is empty
            if (list.isEmpty()) {
                binding.rvFeedList.visibility = View.GONE
                binding.tvNoFeed.visibility = View.VISIBLE
            }

            feedAdapter.submitList(list)
        }
    }

    override fun onFeedItemClick(feed: Feed) {
        feedViewModel.onFeedClicked(feed)
    }
}