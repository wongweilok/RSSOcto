package com.weilok.rssocto.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels

import com.weilok.rssocto.databinding.ActivityEntryContentBinding
import com.weilok.rssocto.viewmodels.EntryContentViewModel

class EntryContentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEntryContentBinding

    private val viewModel: EntryContentViewModel by viewModels()

    // Html settings and CSS
    private val css = "<head>" +
            "<style type='text/css'>" +
            "* {word-break: break-word; max-width: 100%;}" +
            "pre {white-space: pre-wrap;}" +
            "figure {width: auto !important;}" +
            "img {height: auto !important;}" +
            "</style>" +
            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
            "</head>"
    private val bodyStart = "<body>"
    private val bodyEnd = "</body>"
    private val horizontalRule = "<hr>"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntryContentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Content"

        // Enable up button
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        // Entry details use for displaying above content
        val entry = viewModel.entry!!
        val entryTitle = "<h2>${entry.title}</h2><br>"
        val entryDateWithAuthor = StringBuilder(entry.formattedDate)

        if (entry.author.isNotEmpty()) {
            entryDateWithAuthor.apply {
                append("<br>Author - ")
                append(entry.author)
            }
        }

        // Combine into one string
        val html = StringBuilder(css)
            .append(bodyStart)
            .append(entryTitle)
            .append(entryDateWithAuthor)
            .append(horizontalRule)
            .append(viewModel.entryContent!!)
            .append(bodyEnd)
            .toString()

        // Display feed entry content with WebView
        binding.wvEntryContent.apply {
            isHorizontalScrollBarEnabled = false
            loadDataWithBaseURL(
                "",
                html,
                "text/html",
                null,
                null
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}