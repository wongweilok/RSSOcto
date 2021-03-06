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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.weilok.rssocto.data.local.entities.Feed
import com.weilok.rssocto.databinding.FeedItemListBinding

class FeedAdapter(
    private val clickListener: OnFeedItemClickListener,
    private val longClickListener: OnFeedItemLongClickListener
) : ListAdapter<Feed, FeedAdapter.FeedViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FeedItemListBinding
            .inflate(layoutInflater, parent, false)

        return FeedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FeedViewHolder(private val binding: FeedItemListBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                root.apply {
                    setOnClickListener {
                        val position = bindingAdapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            val feed = getItem(position)
                            clickListener.onFeedItemClick(feed)
                        }
                    }

                    setOnLongClickListener {
                        val position = bindingAdapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            val feed = getItem(position)
                            longClickListener.onFeedItemLongClick(feed, it)
                        }
                        return@setOnLongClickListener true
                    }
                }
            }
        }

        fun bind(feed: Feed) {
            binding.apply {
                tvFeedTitle.text = feed.title

                if (feed.imageUrl.isEmpty()) {
                    tvImageFallback.text = feed.title[0].toString()
                } else {
                    Glide.with(itemView)
                        .load(feed.imageUrl)
                        .centerCrop()
                        .circleCrop()
                        .into(ivFeedImage)
                }
            }
        }
    }

    interface OnFeedItemClickListener {
        fun onFeedItemClick(feed: Feed)
    }

    interface OnFeedItemLongClickListener {
        fun onFeedItemLongClick(feed: Feed, v: View)
    }

    class DiffCallback : DiffUtil.ItemCallback<Feed>() {
        override fun areItemsTheSame(oldItem: Feed, newItem: Feed): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Feed, newItem: Feed): Boolean {
            return oldItem == newItem
        }
    }
}