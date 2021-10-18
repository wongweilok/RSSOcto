package com.weilok.rssocto.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.weilok.rssocto.R
import com.weilok.rssocto.databinding.FragmentEntryContentBinding
import com.weilok.rssocto.viewmodels.EntryContentViewModel

class EntryContentFragment : Fragment(R.layout.fragment_entry_content) {
    private lateinit var binding: FragmentEntryContentBinding

    private val viewModel: EntryContentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize binding for current view
        binding = FragmentEntryContentBinding.bind(view)

        // Display feed entry content with WebView
        binding.wvEntryContent.apply {
            isHorizontalScrollBarEnabled = false
            loadDataWithBaseURL(
                "",
                viewModel.entryContent!!,
                "text/html",
                null,
                null
            )
        }
    }
}