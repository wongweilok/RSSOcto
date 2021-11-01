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
import android.widget.PopupMenu
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
class FeedFragment : Fragment(R.layout.fragment_feed),
    FeedAdapter.OnFeedItemClickListener,
    FeedAdapter.OnFeedItemLongClickListener {
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
                    is FeedViewModel.FeedEvent.ShowFeedChangedMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_SHORT).show()
                    }
                    is FeedViewModel.FeedEvent.NavigateToEntryFragment -> {
                        val action = FeedFragmentDirections
                            .actionFeedFragmentToEntryFragment(event.feed, event.feed.title)
                        findNavController().navigate(action)
                    }
                    is FeedViewModel.FeedEvent.ShowRecyclerView -> {
                        if (binding.tvNoFeed.visibility == View.VISIBLE) {
                            binding.apply {
                                rvFeedList.visibility = View.VISIBLE
                                tvNoFeed.visibility = View.GONE
                            }
                        }
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
        val feedAdapter = FeedAdapter(this, this)

        binding.apply {
            rvFeedList.apply {
                adapter = feedAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        feedViewModel.feeds.observe(viewLifecycleOwner) { list ->
            // Display different layout when data is empty
            if (list.isEmpty()) {
                binding.apply {
                    rvFeedList.visibility = View.GONE
                    tvNoFeed.visibility = View.VISIBLE
                }
            }

            feedAdapter.submitList(list)
        }
    }

    override fun onFeedItemClick(feed: Feed) {
        feedViewModel.onFeedClicked(feed)
    }

    override fun onFeedItemLongClick(feed: Feed, v: View) {
        showPopup(v, feed)
    }

    private fun showPopup(v: View, feed: Feed) {
        // Create popup menu
        val popupMenu = PopupMenu(requireContext(), v)
        popupMenu.inflate(R.menu.feed_item_option_menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.optDelete -> {
                    feedViewModel.deleteFeed(feed)

                    return@setOnMenuItemClickListener true
                }

                R.id.optMarkAllAsRead -> {
                    feedViewModel.markAllEntriesAsRead()

                    return@setOnMenuItemClickListener true
                }
                else -> true
            }
        }
        popupMenu.show()
    }
}