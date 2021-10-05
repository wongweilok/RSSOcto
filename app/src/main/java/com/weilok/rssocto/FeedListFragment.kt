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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.weilok.rssocto.adapter.FeedAdapter
import com.weilok.rssocto.data.AppRepository
import com.weilok.rssocto.data.local.AppDatabase
import com.weilok.rssocto.databinding.FragmentFeedListBinding
import com.weilok.rssocto.viewmodel.FeedViewModel
import com.weilok.rssocto.viewmodel.FeedViewModelFactory

class FeedListFragment : Fragment(R.layout.fragment_feed_list) {
    private lateinit var binding: FragmentFeedListBinding
    private lateinit var feedViewModel: FeedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize binding for current view
        binding = FragmentFeedListBinding.bind(view)

        // Initialize database
        val feedDao = AppDatabase.getInstance(requireContext()).feedDao
        val entryDao = AppDatabase.getInstance(requireContext()).entryDao

        val repo = AppRepository(feedDao, entryDao)
        val factory = FeedViewModelFactory(repo)

        // Initialize FeedListViewModel
        feedViewModel = ViewModelProvider(requireActivity(), factory)
            .get(FeedViewModel::class.java)

        initRecyclerView()
        initEmptyLayoutBtn()
    }

    private fun initRecyclerView() {
        binding.rvFeedList.layoutManager = LinearLayoutManager(requireContext())

        feedViewModel.feeds.observe(viewLifecycleOwner, {
            Log.i("LocalFeeds", it.toString())
            binding.rvFeedList.adapter = FeedAdapter(it)

            // Display different layout when data is empty
            if (it.isEmpty()) {
                binding.rvFeedList.visibility = View.GONE
                binding.emptyDataLayout.root.visibility = View.VISIBLE
            } else {
                binding.rvFeedList.visibility = View.VISIBLE
                binding.emptyDataLayout.root.visibility = View.GONE
            }
        })
    }

    private fun initEmptyLayoutBtn() {
        // Navigate to AddFeedFragment
        binding.emptyDataLayout.btnAddFeed.setOnClickListener {
            val action = FeedListFragmentDirections.actionFeedListFragmentToAddFeedFragment()
            findNavController().navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()

        binding.rvFeedList.visibility = View.VISIBLE
        binding.emptyDataLayout.root.visibility = View.GONE
    }
}