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

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

import com.weilok.rssocto.databinding.ActivityAddFeedBinding
import com.weilok.rssocto.viewmodels.AddFeedViewModel

const val RESULT_CODE = Activity.RESULT_FIRST_USER + 7

@AndroidEntryPoint
class AddFeedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddFeedBinding

    private val viewModel: AddFeedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Add Feed"

        // Enable up button
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        binding.apply {
            addFeedVM = viewModel
            lifecycleOwner = this@AddFeedActivity

            btnAdd.setOnClickListener {
                viewModel.getFeed()
            }
        }

        // Observe whether the given URL is valid or not
        viewModel.urlValidation.observe(this) {
            Log.i("URLValid", it)
            if (it != "") {
                binding.tilFeedUrl.error = it
            } else {
                binding.tilFeedUrl.error = null
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.addFeedEvent.collect { event ->
                when (event) {
                    is AddFeedViewModel.AddFeedEvent.AddAndNavigateBack -> {
                        binding.apply {
                            etFeedUrl.clearFocus()
                            etFeedName.clearFocus()

                            val intent = Intent()
                            intent.putExtra("add_feed_request", event.result)
                            setResult(RESULT_CODE, intent)

                            super.onBackPressed()
                        }
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}