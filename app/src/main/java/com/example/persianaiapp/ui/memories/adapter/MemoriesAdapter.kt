package com.example.persianaiapp.ui.memories.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.persianaiapp.data.model.Memory
import com.example.persianaiapp.databinding.ItemMemoryBinding
import java.text.SimpleDateFormat
import java.util.*

class MemoriesAdapter(
    private val onMemoryClick: (Memory) -> Unit,
    private val onMemoryLongClick: (Memory) -> Unit,
    private val onPinClick: (Memory) -> Unit
) : ListAdapter<Memory, MemoriesAdapter.MemoryViewHolder>(MemoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoryViewHolder {
        val binding = ItemMemoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MemoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemoryViewHolder, position: Int) {
        val memory = getItem(position)
        holder.bind(memory)
    }

    inner class MemoryViewHolder(
        private val binding: ItemMemoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(memory: Memory) {
            binding.apply {
                // Set memory data
                titleText.text = memory.title
                contentText.text = memory.content
                dateText.text = formatDate(memory.updatedAt)
                
                // Set pin state
                pinIcon.setImageResource(
                    if (memory.isPinned) {
                        R.drawable.ic_pin_24
                    } else {
                        R.drawable.ic_pin_outline_24
                    }
                )
                
                // Set click listeners
                root.setOnClickListener {
                    onMemoryClick(memory)
                }
                
                root.setOnLongClickListener {
                    onMemoryLongClick(memory)
                    true
                }
                
                pinIcon.setOnClickListener {
                    onPinClick(memory)
                }
                
                // Show tags if available
                if (memory.tags.isNotEmpty()) {
                    tagsLayout.visibility = View.VISIBLE
                    tagsText.text = memory.tags.joinToString(" • ")
                } else {
                    tagsLayout.visibility = View.GONE
                }
            }
        }
        
        private fun formatDate(date: Date): String {
            return SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date)
        }
    }
}

class MemoryDiffCallback : DiffUtil.ItemCallback<Memory>() {
    override fun areItemsTheSame(oldItem: Memory, newItem: Memory): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Memory, newItem: Memory): Boolean {
        return oldItem == newItem
    }
}
