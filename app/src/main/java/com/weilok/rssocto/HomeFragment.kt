package com.weilok.rssocto

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.weilok.rssocto.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)
        binding.btnAddFeed.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToAddFeedFragment()
            findNavController().navigate(action)
        }
    }
}