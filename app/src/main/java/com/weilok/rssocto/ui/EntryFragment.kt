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
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
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
import com.weilok.rssocto.data.EntriesView
import com.weilok.rssocto.data.PrefHandler
import com.weilok.rssocto.data.local.entities.Entry
import com.weilok.rssocto.databinding.FragmentEntryBinding
import com.weilok.rssocto.viewmodels.EntryViewModel

@AndroidEntryPoint
class EntryFragment : Fragment(R.layout.fragment_entry),
    EntryAdapter.OnEntryItemClickListener,
    EntryAdapter.OnEntryItemLongClickListener {
    private lateinit var binding: FragmentEntryBinding

    private val viewModel: EntryViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize binding for current view
        binding = FragmentEntryBinding.bind(view)

        /*
         * Initialize adapter and restore list item position when
         * returning to this fragment.
         */
        val entryAdapter = EntryAdapter(this, this)
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
                viewModel.refreshFeedEntries()
                swipeRefresh.isRefreshing = false
            }
        }

        setHasOptionsMenu(true)

        // Observe and display entries
        viewModel.entries.observe(viewLifecycleOwner) { list ->
            if (list.isEmpty()) {
                binding.apply {
                    tvNoEntry.visibility = View.VISIBLE
                }
            } else {
                binding.apply {
                    tvNoEntry.visibility = View.GONE
                }
            }

            entryAdapter.submitList(list)
        }

        // Collect Signal from Event Channel
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.entryEvent.collect { event ->
                when (event) {
                    is EntryViewModel.EntryEvent.NavigateToContentView -> {
                        val action = EntryFragmentDirections.actionEntryFragmentToEntryContentActivity(event.entry)
                        findNavController().navigate(action)
                    }
                    is EntryViewModel.EntryEvent.ShowRefreshMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onEntryItemClick(entry: Entry) {
        viewModel.onEntryClicked(entry)
    }

    override fun onEntryItemLongClick(entry: Entry, v: View) {
        showPopup(v, entry)
    }

    private fun showPopup(v: View, entry: Entry) {
        // Create popup menu
        val popMenu = PopupMenu(requireContext(), v)
        popMenu.inflate(R.menu.entry_item_option_menu)
        if (entry.read) {
            popMenu.menu.getItem(0).setTitle(R.string.opt_mark_as_unread)
        } else {
            popMenu.menu.getItem(0).setTitle(R.string.opt_mark_as_read)
        }
        popMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.optMarkAsRead -> {
                    viewModel.markEntryAs(entry)

                    return@setOnMenuItemClickListener true
                }
                else -> true
            }
        }
        popMenu.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.entry_item_list_option_menu, menu)

        val entryFilterPref = PrefHandler(requireContext()).getEntryFilterPref()

        if (entryFilterPref == EntriesView.BY_ALL) {
            menu.findItem(R.id.optAll).isChecked = true
        } else if (entryFilterPref == EntriesView.BY_UNREAD) {
            menu.findItem(R.id.optUnread).isChecked = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Choose to show all entries or unread entries
        return when (item.itemId) {
            R.id.optAll -> {
                item.isChecked = true
                //viewModel.onEntriesViewSelected(EntriesView.BY_ALL)
                viewModel.entryFilter.value = EntriesView.BY_ALL
                true
            }
            R.id.optUnread -> {
                item.isChecked = true
                //viewModel.onEntriesViewSelected(EntriesView.BY_UNREAD)
                viewModel.entryFilter.value = EntriesView.BY_UNREAD
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}