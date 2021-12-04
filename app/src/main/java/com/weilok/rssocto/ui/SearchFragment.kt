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
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

import com.weilok.rssocto.R
import com.weilok.rssocto.adapter.EntryAdapter
import com.weilok.rssocto.data.local.entities.Entry
import com.weilok.rssocto.databinding.FragmentSearchBinding
import com.weilok.rssocto.viewmodels.SearchViewModel

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search), SearchView.OnQueryTextListener, EntryAdapter.OnEntryItemClickListener {
    private lateinit var binding: FragmentSearchBinding

    private val viewModel: SearchViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize binding for current view
        binding = FragmentSearchBinding.bind(view)

        setHasOptionsMenu(true)

        initRecyclerView()

        // Collect Signal from Event Channel
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.searchEvent.collect { event ->
                when (event) {
                    is SearchViewModel.SearchEvent.NavigateToContentView -> {
                        val action = SearchFragmentDirections.actionSearchFragmentToEntryContentActivity(event.entry)
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        val entryAdapter = EntryAdapter(this)

        binding.apply {
            rvQueryFeedList.apply {
                adapter = entryAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewModel.entries.observe(viewLifecycleOwner) { list ->
            if (list.isEmpty()) {
                binding.apply {
                    tvSearchNoResult.visibility = View.VISIBLE
                }
            } else {
                binding.apply {
                    tvSearchNoResult.visibility = View.GONE
                }
            }

            entryAdapter.submitList(list)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.optSearch)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        viewModel.searchQuery.value = query.orEmpty()

        if (query.isNullOrEmpty()) {
            binding.apply {
                rvQueryFeedList.visibility = View.GONE
                tvSearch.visibility = View.VISIBLE
            }
        } else {
            binding.apply {
                rvQueryFeedList.visibility = View.VISIBLE
                tvSearch.visibility = View.GONE
            }
        }

        return true
    }

    override fun onEntryItemClick(entry: Entry) {
        viewModel.onEntryClicked(entry)
    }
}