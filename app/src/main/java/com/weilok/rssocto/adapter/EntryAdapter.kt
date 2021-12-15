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

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.weilok.rssocto.data.local.entities.Entry
import com.weilok.rssocto.databinding.EntryItemListBinding

class EntryAdapter(
    private val listener: OnEntryItemClickListener,
    private val longClickListener: OnEntryItemLongClickListener
) : ListAdapter<Entry, EntryAdapter.EntryViewHolder>(DiffCallback()) {
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
                            val entry = getItem(position)
                            listener.onEntryItemClick(entry)
                        }
                    }

                    setOnLongClickListener { view ->
                        val position = bindingAdapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            val entry = getItem(position)
                            longClickListener.onEntryItemLongClick(entry, view)
                        }

                        return@setOnLongClickListener true
                    }
                }
            }
        }

        fun bind(entry: Entry) {
            binding.apply {
                tvEntryTitle.apply {
                    isEnabled = !entry.read
                    text = entry.title
                }

                tvPubDate.apply {
                    isEnabled = !entry.read
                    text = DateUtils.getRelativeTimeSpanString(entry.date.time)
                }
            }
        }
    }

    interface OnEntryItemClickListener {
        fun onEntryItemClick(entry: Entry)
    }

    interface OnEntryItemLongClickListener {
        fun onEntryItemLongClick(entry: Entry, v: View)
    }

    class DiffCallback : DiffUtil.ItemCallback<Entry>() {
        override fun areItemsTheSame(oldItem: Entry, newItem: Entry): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Entry, newItem: Entry): Boolean {
            return oldItem == newItem
        }
    }
}