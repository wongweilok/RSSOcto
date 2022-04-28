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

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

import com.weilok.rssocto.R
import com.weilok.rssocto.adapter.EntryAdapter
import com.weilok.rssocto.data.local.entities.Entry
import com.weilok.rssocto.databinding.FragmentSearchBinding
import com.weilok.rssocto.viewmodels.SearchViewModel

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search),
    SearchView.OnQueryTextListener,
    EntryAdapter.OnEntryItemClickListener,
    EntryAdapter.OnEntryItemLongClickListener,
    EntryAdapter.OnFavIconClickListener {
    private lateinit var binding: FragmentSearchBinding

    private val viewModel: SearchViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize binding for current view
        binding = FragmentSearchBinding.bind(view)

        // Enable option menu
        setHasOptionsMenu(true)

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
        val entryAdapter = EntryAdapter(this,this, this)

        binding.apply {
            rvQueryFeedList.apply {
                adapter = entryAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewModel.entries.observe(viewLifecycleOwner) { list ->
            if (list.isEmpty()) {
                binding.tvSearchNoResult.visibility = View.VISIBLE
            } else {
                binding.tvSearchNoResult.visibility = View.GONE
            }

            entryAdapter.submitList(list)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.optSearch)
        val searchView = searchItem.actionView as SearchView

        // Get bottom navigation bar from activity
        val botNavBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                botNavBar.visibility = View.GONE
                binding.apply {
                    tvSearch.visibility = View.GONE
                    rvQueryFeedList.visibility = View.VISIBLE
                }

                initRecyclerView()

                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                botNavBar.visibility = View.VISIBLE
                binding.apply {
                    tvSearch.visibility = View.VISIBLE
                    rvQueryFeedList.visibility = View.GONE
                    tvSearchNoResult.visibility = View.GONE
                }

                return true
            }
        })

        searchView.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    // Search article in real time with given query
    override fun onQueryTextChange(query: String?): Boolean {
        viewModel.searchQuery.value = query.orEmpty()

        return true
    }

    override fun onEntryItemClick(entry: Entry) {
        viewModel.onEntryClicked(entry)
    }

    override fun onEntryItemLongClick(entry: Entry, v: View) {
    }

    override fun onFavIconClick(entry: Entry) {
        viewModel.onFavIconClicked(entry)
    }
}