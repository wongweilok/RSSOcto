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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

import com.weilok.rssocto.R
import com.weilok.rssocto.adapter.EntryAdapter
import com.weilok.rssocto.data.local.entities.Entry
import com.weilok.rssocto.databinding.FragmentEntryBinding
import com.weilok.rssocto.viewmodels.EntryViewModel

@AndroidEntryPoint
class EntryFragment : Fragment(R.layout.fragment_entry), EntryAdapter.OnEntryItemClickListener {
    private lateinit var binding: FragmentEntryBinding

    private val entryViewModel: EntryViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentEntryBinding.bind(view)

        // Initialize binding for current view
        val entryAdapter = EntryAdapter(this)
        entryAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        binding.apply {
            // Initialize RecyclerView
            rvEntryList.apply {
                adapter = entryAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            // Initialize SwipeRefreshLayout action
            swipeRefresh.setOnRefreshListener {
                // Refresh RecyclerView
                entryViewModel.refreshFeedEntries()
                swipeRefresh.isRefreshing = false
            }
        }

        // Get entries with corresponding feed
        entryViewModel.getFeedWithEntries(entryViewModel.feedId!!)

        // Collect Signal from Event Channel
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            entryViewModel.entryEvent.collect { event ->
                when (event) {
                    is EntryViewModel.EntryEvent.ListOfEntries -> {
                        entryAdapter.submitList(event.list)
                    }
                    is EntryViewModel.EntryEvent.NavigateToContentFragment -> {
                        val action = EntryFragmentDirections.actionEntryFragmentToEntryContentFragment(event.entry)
                        findNavController().navigate(action)
                    }
                    is EntryViewModel.EntryEvent.ShowRefreshMessage -> {
                        entryViewModel.getFeedWithEntries(entryViewModel.feedId!!)
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onEntryItemClick(entry: Entry) {
        entryViewModel.onEntryClicked(entry)
    }
}