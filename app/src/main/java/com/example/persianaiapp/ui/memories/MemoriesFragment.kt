package com.example.persianaiapp.ui.memories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.persianaiapp.R
import com.example.persianaiapp.data.model.Memory
import com.example.persianaiapp.databinding.FragmentMemoriesBinding
import com.example.persianaiapp.ui.memories.adapter.MemoriesAdapter
import com.example.persianaiapp.util.collectLatestIn
import com.example.persianaiapp.util.showSnackbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MemoriesFragment : Fragment() {

    private var _binding: FragmentMemoriesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MemoriesViewModel by viewModels()
    private lateinit var adapter: MemoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMemoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        setupSearchView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = MemoriesAdapter(
            onMemoryClick = { memory ->
                // Navigate to memory detail
                findNavController().navigate(
                    MemoriesFragmentDirections.actionMemoriesToMemoryDetail(memory.id)
                )
            },
            onMemoryLongClick = { memory ->
                showMemoryOptions(memory)
            },
            onPinClick = { memory ->
                viewModel.togglePinMemory(memory)
            }
        )
        
        binding.recyclerView.apply {
            setHasFixedSize(true)
            this.adapter = this@MemoriesFragment.adapter
        }
    }

    private fun setupClickListeners() {
        binding.fabAddMemory.setOnClickListener {
            // Navigate to add memory screen
            findNavController().navigate(
                MemoriesFragmentDirections.actionMemoriesToAddMemory()
            )
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchMemories(newText ?: "")
                return true
            }
        })
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is MemoriesViewModel.MemoriesUiState.Loading -> {
                        showLoading()
                    }
                    is MemoriesViewModel.MemoriesUiState.Empty -> {
                        showEmptyView()
                    }
                    is MemoriesViewModel.MemoriesUiState.EmptySearch -> {
                        showEmptySearch(state.query)
                    }
                    is MemoriesViewModel.MemoriesUiState.Success -> {
                        showMemories(state.memories)
                    }
                    is MemoriesViewModel.MemoriesUiState.Error -> {
                        showError(state.message)
                    }
                }
            }
        }

        // Observe events
        viewModel.events.collectLatestIn(viewLifecycleOwner) { event ->
            when (event) {
                is MemoriesViewModel.UiEvent.ShowMessage -> {
                    showSnackbar(event.message)
                }
                is MemoriesViewModel.UiEvent.NavigateToMemory -> {
                    // Navigate to memory detail
                    findNavController().navigate(
                        MemoriesFragmentDirections.actionMemoriesToMemoryDetail(event.memoryId)
                    )
                }
                MemoriesViewModel.UiEvent.NavigateToAddMemory -> {
                    // Navigate to add memory
                    findNavController().navigate(
                        MemoriesFragmentDirections.actionMemoriesToAddMemory()
                    )
                }
            }
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.GONE
            searchView.visibility = View.GONE
        }
    }

    private fun showEmptyView() {
        binding.apply {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
            searchView.visibility = View.VISIBLE
            
            emptyViewText.text = getString(R.string.no_memories_found)
            emptyViewButton.visibility = View.VISIBLE
            emptyViewButton.setOnClickListener {
                viewModel.loadMemories()
            }
        }
    }

    private fun showEmptySearch(query: String) {
        binding.apply {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
            searchView.visibility = View.VISIBLE
            
            emptyViewText.text = getString(R.string.no_results_for_query, query)
            emptyViewButton.visibility = View.GONE
        }
    }

    private fun showMemories(memories: List<Memory>) {
        binding.apply {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            searchView.visibility = View.VISIBLE
            
            adapter.submitList(memories)
        }
    }

    private fun showError(message: String) {
        binding.apply {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
            searchView.visibility = View.VISIBLE
            
            emptyViewText.text = message
            emptyViewButton.visibility = View.VISIBLE
            emptyViewButton.text = getString(R.string.retry)
            emptyViewButton.setOnClickListener {
                viewModel.loadMemories()
            }
        }
    }

    private fun showMemoryOptions(memory: Memory) {
        val options = arrayOf(
            if (memory.isPinned) getString(R.string.unpin) else getString(R.string.pin),
            getString(R.string.edit),
            getString(R.string.delete)
        )

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(memory.title)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> viewModel.togglePinMemory(memory)
                    1 -> {
                        // Navigate to edit memory
                        findNavController().navigate(
                            MemoriesFragmentDirections.actionMemoriesToEditMemory(memory.id)
                        )
                    }
                    2 -> showDeleteConfirmation(memory)
                }
            }
            .show()
    }

    private fun showDeleteConfirmation(memory: Memory) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_memory)
            .setMessage(R.string.are_you_sure_you_want_to_delete_this_memory)
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteMemory(memory)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showSnackbar(message: String) {
        binding.root.showSnackbar(message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
