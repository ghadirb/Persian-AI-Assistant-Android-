package com.example.persianaiapp.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.persianaiapp.R
import com.example.persianaiapp.data.model.ChatMessage
import com.example.persianaiapp.databinding.FragmentChatBinding
import com.example.persianaiapp.ui.chat.adapter.ChatAdapter
import com.example.persianaiapp.util.collectLatestIn
import com.example.persianaiapp.util.hideKeyboard
import com.example.persianaiapp.util.showSnackbar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        binding.messagesRecyclerView.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            setHasFixedSize(true)
        }
    }

    private fun setupClickListeners() {
        // Send message on click
        binding.inputLayout.setEndIconOnClickListener {
            sendMessage()
        }

        // Send message on keyboard action
        binding.messageEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                return@setOnEditorActionListener true
            }
            false
        }

        // Voice input
        binding.voiceInputFab.setOnClickListener {
            // TODO: Implement voice input
            showSnackbar("Voice input will be implemented soon")
        }

        // Pull to refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadMoreMessages()
        }
    }

    private fun sendMessage() {
        val message = binding.messageEditText.text?.toString()?.trim()
        if (!message.isNullOrEmpty()) {
            viewModel.sendMessage(message)
            binding.messageEditText.text?.clear()
            hideKeyboard()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            // Observe messages
            viewModel.uiState.collectLatest { state ->
                // Update messages
                chatAdapter.submitList(state.messages) {
                    // Scroll to bottom when new message arrives
                    if (state.messages.isNotEmpty()) {
                        binding.messagesRecyclerView.smoothScrollToPosition(state.messages.size - 1)
                    }
                }

                // Update loading state
                binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                binding.swipeRefreshLayout.isRefreshing = state.isLoading

                // Handle recording state
                updateVoiceInputState(state.isRecording)
            }
        }

        // Observe events
        viewModel.events.collectLatestIn(viewLifecycleOwner) { event ->
            when (event) {
                is ChatViewModel.UiEvent.ShowMessage -> {
                    showSnackbar(event.message)
                }
                is ChatViewModel.UiEvent.NavigateToMemory -> {
                    // TODO: Navigate to memory detail
                }
            }
        }
    }

    private fun updateVoiceInputState(isRecording: Boolean) {
        binding.voiceInputFab.apply {
            if (isRecording) {
                setImageResource(R.drawable.ic_stop_24)
                contentDescription = getString(R.string.stop_recording)
                // TODO: Add visual feedback for recording
            } else {
                setImageResource(R.drawable.ic_mic_24)
                contentDescription = getString(R.string.voice_input)
                // TODO: Remove visual feedback for recording
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun hideKeyboard() {
        binding.messageEditText.clearFocus()
        hideKeyboard(requireActivity())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
