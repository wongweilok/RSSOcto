package com.weilok.rssocto.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.weilok.rssocto.data.local.entities.Entry
import com.weilok.rssocto.databinding.EntryItemListBinding

class EntryAdapter(
    private val listener: OnEntryItemClickListener
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
                root.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val entry = getItem(position)
                        listener.onEntryItemClick(entry)
                    }
                }
            }
        }

        fun bind(entry: Entry) {
            binding.apply {
                tvEntryTitle.text = entry.title
                tvPubDate.text = DateUtils.getRelativeTimeSpanString(entry.date.time)
            }
        }
    }

    interface OnEntryItemClickListener {
        fun onEntryItemClick(entry: Entry)
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