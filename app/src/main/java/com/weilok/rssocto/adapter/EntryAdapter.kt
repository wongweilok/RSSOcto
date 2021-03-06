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

package com.weilok.rssocto.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weilok.rssocto.R

import com.weilok.rssocto.data.local.entities.Entry
import com.weilok.rssocto.data.local.entities.EntryWithFeed
import com.weilok.rssocto.databinding.EntryItemListBinding

class EntryAdapter(
    private val listener: OnEntryClickListener
) : ListAdapter<EntryWithFeed, EntryAdapter.EntryViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = EntryItemListBinding
            .inflate(layoutInflater, parent, false)

        return EntryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EntryViewHolder(private val binding: EntryItemListBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                root.apply {
                    setOnClickListener {
                        val position = bindingAdapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            val entryWithFeed = getItem(position)
                            listener.onEntryItemClick(entryWithFeed.entry)
                        }
                    }

                    setOnLongClickListener { view ->
                        val position = bindingAdapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            val entryWithFeed = getItem(position)
                            listener.onEntryItemLongClick(entryWithFeed.entry, view)
                        }

                        return@setOnLongClickListener true
                    }
                }

                iconFav.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val entryWithFeed = getItem(position)
                        if (entryWithFeed.entry.favorite) {
                            iconFav.setImageResource(R.drawable.ic_favorite_border)
                        } else {
                            iconFav.setImageResource(R.drawable.ic_favorite)
                        }

                        listener.onFavIconClick(entryWithFeed.entry)
                    }
                }
            }
        }

        fun bind(entryWihFeed: EntryWithFeed) {
            binding.apply {
                tvEntryTitle.apply {
                    isEnabled = !entryWihFeed.entry.read
                    text = entryWihFeed.entry.title
                }

                tvPubDate.apply {
                    isEnabled = !entryWihFeed.entry.read
                    text = DateUtils.getRelativeTimeSpanString(entryWihFeed.entry.date.time)
                }

                val mainImage = entryWihFeed.entry.imageUrl.ifEmpty {
                    entryWihFeed.feed.imageUrl
                }

                if (mainImage.isEmpty()) {
                    tvImageFallback.text = entryWihFeed.feed.title[0].toString()

                    Glide.with(itemView)
                        .clear(ivEntryImage)
                } else {
                    tvImageFallback.text = ""

                    Glide.with(itemView)
                        .load(mainImage)
                        .centerCrop()
                        .circleCrop()
                        .into(ivEntryImage)
                }

                if (entryWihFeed.entry.favorite) {
                    iconFav.setImageResource(R.drawable.ic_favorite)
                } else {
                    iconFav.setImageResource(R.drawable.ic_favorite_border)
                }
            }
        }
    }

    interface OnEntryClickListener {
        fun onEntryItemClick(entry: Entry)
        fun onEntryItemLongClick(entry: Entry, v: View)
        fun onFavIconClick(entry: Entry)
    }

    class DiffCallback : DiffUtil.ItemCallback<EntryWithFeed>() {
        override fun areItemsTheSame(oldItem: EntryWithFeed, newItem: EntryWithFeed): Boolean {
            return oldItem.entry.url == newItem.entry.url
        }

        override fun areContentsTheSame(oldItem: EntryWithFeed, newItem: EntryWithFeed): Boolean {
            return oldItem == newItem
        }
    }
}