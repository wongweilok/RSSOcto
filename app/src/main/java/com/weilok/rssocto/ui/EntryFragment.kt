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
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint

import com.weilok.rssocto.R
import com.weilok.rssocto.adapter.EntryAdapter
import com.weilok.rssocto.databinding.FragmentEntryBinding
import com.weilok.rssocto.viewmodels.EntryViewModel
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class EntryFragment : Fragment(R.layout.fragment_entry) {
    private lateinit var binding: FragmentEntryBinding

    private val entryViewModel: EntryViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentEntryBinding.bind(view)

        // Initialize binding for current view
        val entryAdapter = EntryAdapter()

        // Initialize RecyclerView
        binding.apply {
            rvEntryList.apply {
                adapter = entryAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
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
                }
            }
        }
    }
}