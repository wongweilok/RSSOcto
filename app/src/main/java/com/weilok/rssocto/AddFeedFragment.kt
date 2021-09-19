package com.weilok.rssocto

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.weilok.rssocto.data.AppRepository
import com.weilok.rssocto.databinding.FragmentAddFeedBinding
import com.weilok.rssocto.viewmodel.FeedViewModel
import com.weilok.rssocto.viewmodel.FeedViewModelFactory

class AddFeedFragment : Fragment(R.layout.fragment_add_feed) {
    private lateinit var binding: FragmentAddFeedBinding
    private lateinit var feedViewModel: FeedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddFeedBinding.bind(view)

        val repo = AppRepository()
        val factory = FeedViewModelFactory(repo)

        feedViewModel = ViewModelProvider(this, factory)
            .get(FeedViewModel::class.java)

        binding.feedVM = feedViewModel

        initFeedObserver()
    }

    private fun initFeedObserver() {
        feedViewModel.remoteAtomFeed.observe(viewLifecycleOwner) {
            Log.i("RemoteFeed", it.toString())
        }
    }
}