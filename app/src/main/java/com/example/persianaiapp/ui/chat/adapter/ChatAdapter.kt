package com.example.persianaiapp.ui.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.persianaiapp.data.model.ChatMessage
import com.example.persianaiapp.databinding.ItemMessageReceivedBinding
import com.example.persianaiapp.databinding.ItemMessageSentBinding
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter : ListAdapter<ChatMessage, RecyclerView.ViewHolder>(ChatDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isFromUser) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val binding = ItemMessageSentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            SentMessageViewHolder(binding)
        } else {
            val binding = ItemMessageReceivedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            ReceivedMessageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is SentMessageViewHolder -> holder.bind(message)
            is ReceivedMessageViewHolder -> holder.bind(message)
        }
    }

    inner class SentMessageViewHolder(
        private val binding: ItemMessageSentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: ChatMessage) {
            binding.apply {
                messageText.text = message.content
                timeText.text = formatTime(message.timestamp)
                
                // Show loading indicator for pending messages
                if (message.isPending) {
                    progressBar.visibility = View.VISIBLE
                    messageText.alpha = 0.7f
                } else {
                    progressBar.visibility = View.GONE
                    messageText.alpha = 1f
                }
                
                // Show error indicator if message failed to send
                if (message.isError) {
                    errorIndicator.visibility = View.VISIBLE
                    messageText.alpha = 0.7f
                    root.setOnClickListener {
                        // TODO: Retry sending the message
                    }
                } else {
                    errorIndicator.visibility = View.GONE
                    root.setOnClickListener(null)
                }
            }
        }
    }

    inner class ReceivedMessageViewHolder(
        private val binding: ItemMessageReceivedBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: ChatMessage) {
            binding.apply {
                messageText.text = message.content
                timeText.text = formatTime(message.timestamp)
                
                // TODO: Set AI avatar
                // avatarImage.setImageResource(R.drawable.ic_ai_avatar)
                
                // Handle message actions
                root.setOnLongClickListener {
                    // TODO: Show message options (copy, save, etc.)
                    true
                }
            }
        }
    }

    private fun formatTime(timestamp: Long): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
    }
}

class ChatDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
    override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
        return oldItem == newItem
    }
}
