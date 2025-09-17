package com.example.persianaiapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.persianaiapp.databinding.FragmentHomeBinding
import com.example.persianaiapp.util.collectLatestIn
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.apply {
            // Setup refresh layout
            swipeRefreshLayout.setOnRefreshListener {
                viewModel.refresh()
            }

            // Setup mode toggle
            modeToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    viewModel.setMode(
                        when (checkedId) {
                            R.id.btn_online -> HomeViewModel.Mode.ONLINE
                            R.id.btn_offline -> HomeViewModel.Mode.OFFLINE
                            else -> HomeViewModel.Mode.ONLINE
                        }
                    )
                }
            }

            // Setup voice input button
            btnVoiceInput.setOnClickListener {
                viewModel.onVoiceInputClick()
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uiState.collectLatest { state ->
                binding.apply {
                    // Update loading state
                    swipeRefreshLayout.isRefreshing = state.isLoading

                    // Update mode
                    when (state.mode) {
                        HomeViewModel.Mode.ONLINE -> {
                            modeToggleGroup.check(R.id.btn_online)
                            tvModeStatus.text = getString(R.string.online_mode)
                        }
                        HomeViewModel.Mode.OFFLINE -> {
                            modeToggleGroup.check(R.id.btn_offline)
                            tvModeStatus.text = getString(R.string.offline_mode)
                        }
                    }

                    // Update connection status
                    tvConnectionStatus.text = state.connectionStatus
                    tvConnectionStatus.setTextColor(
                        when (state.isConnected) {
                            true -> resources.getColor(android.R.color.holo_green_dark, null)
                            false -> resources.getColor(android.R.color.holo_red_dark, null)
                        }
                    )

                    // Update recent memories
                    memoriesRecyclerView.withModels {
                        state.recentMemories.forEach { memory ->
                            // TODO: Add memory item view holder
                        }
                    }
                }
            }
        }

        // Collect UI events
        viewModel.events.collectLatestIn(viewLifecycleOwner) { event ->
            when (event) {
                is HomeViewModel.UiEvent.ShowMessage -> {
                    Snackbar.make(requireView(), event.message, Snackbar.LENGTH_SHORT).show()
                }
                is HomeViewModel.UiEvent.RequestVoiceInput -> {
                    // TODO: Handle voice input request
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
