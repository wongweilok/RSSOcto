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

package com.weilok.rssocto.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.weilok.rssocto.data.local.entities.Feed
import com.weilok.rssocto.databinding.FeedItemListBinding

class FeedAdapter(private val feedList: List<Feed>) : RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {
    class FeedViewHolder(private val binding: FeedItemListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(feed: Feed) {
            binding.tvFeedTitle.text = feed.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FeedItemListBinding
            .inflate(layoutInflater, parent, false)

        return FeedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.bind(feedList[position])
    }

    override fun getItemCount(): Int {
        return feedList.size
    }
}