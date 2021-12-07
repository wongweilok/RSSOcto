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
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

import com.weilok.rssocto.R
import com.weilok.rssocto.databinding.FragmentAddFeedBinding
import com.weilok.rssocto.viewmodels.AddFeedViewModel

@AndroidEntryPoint
class AddFeedFragment : Fragment(R.layout.fragment_add_feed) {
    private lateinit var binding: FragmentAddFeedBinding
    private lateinit var botNavBar: BottomNavigationView

    private val viewModel: AddFeedViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize binding for current view
        binding = FragmentAddFeedBinding.bind(view)

        binding.apply {
            addFeedVM = viewModel
            lifecycleOwner = activity
        }

        /*
         * Get bottom navigation bar from activity.
         * Hide the bar when this fragment created.
         */
        botNavBar = activity!!.findViewById(R.id.bottom_nav)
        botNavBar.visibility = View.GONE

        // Observe whether the given URL is valid or not
        viewModel.urlValidation.observe(viewLifecycleOwner) {
            Log.i("URLValid", it)
            if (it != "") {
                binding.tilFeedUrl.error = it
            } else {
                binding.tilFeedUrl.error = null
            }
        }

        initButtons()

        // Collect Signal from Event Channel
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addFeedEvent.collect { event ->
                when (event) {
                    is AddFeedViewModel.AddFeedEvent.AddAndNavigateBack -> {
                        binding.etFeedUrl.clearFocus()
                        // Send request to other fragment
                        setFragmentResult(
                            "add_feed_request",
                            bundleOf("add_feed_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun initButtons() {
        binding.btnAdd.setOnClickListener {
            viewModel.getFeed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Enable bottom navigation bar when this fragment destroy.
        botNavBar.visibility = View.VISIBLE
    }
}