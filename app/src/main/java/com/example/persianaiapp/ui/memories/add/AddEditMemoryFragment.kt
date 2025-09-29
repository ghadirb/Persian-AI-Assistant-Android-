package com.example.persianaiapp.ui.memories.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.persianaiapp.R
import com.example.persianaiapp.databinding.FragmentAddEditMemoryBinding
import com.example.persianaiapp.ui.memories.add.AddEditMemoryViewModel.AddEditMemoryUiState
import com.example.persianaiapp.util.showSnackbar
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddEditMemoryFragment : Fragment() {

    private var _binding: FragmentAddEditMemoryBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AddEditMemoryViewModel by viewModels()
    private val args: AddEditMemoryFragmentArgs by navArgs()
    
    private var isEditing = false
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditMemoryBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        setupInputFields()
        setupTags()
        observeViewModel()
    }
    
    private fun setupToolbar() {
        // Set up the toolbar menu
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_add_edit_memory, menu)
            }
            
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_save -> {
                        viewModel.saveMemory()
                        true
                    }
                    android.R.id.home -> {
                        findNavController().navigateUp()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        
        // Set up the toolbar title based on whether we're adding or editing
        val title = if (args.memoryId == -1L) {
            getString(R.string.add_memory)
        } else {
            getString(R.string.edit_memory)
        }
        
        binding.toolbar.title = title
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun setupInputFields() {
        // Title text change listener
        binding.titleEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.updateTitle(text.toString())
        }
        
        // Content text change listener
        binding.contentEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.updateContent(text.toString())
        }
        
        // Pin button click listener
        binding.pinButton.setOnClickListener {
            viewModel.togglePinned()
        }
    }
    
    private fun setupTags() {
        binding.addTagButton.setOnClickListener {
            val tag = binding.tagInputEditText.text.toString().trim()
            if (tag.isNotEmpty()) {
                addTag(tag)
                binding.tagInputEditText.text?.clear()
            }
        }
        
        // Handle tag input submission
        binding.tagInputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                val tag = binding.tagInputEditText.text.toString().trim()
                if (tag.isNotEmpty()) {
                    addTag(tag)
                    binding.tagInputEditText.text?.clear()
                }
                true
            } else {
                false
            }
        }
    }
    
    private fun addTag(tagText: String) {
        val currentTags = (viewModel.uiState.value as? AddEditMemoryUiState.Editing)
            ?.memory?.tags?.toMutableList() ?: mutableListOf()
            
        val normalizedTag = if (tagText.startsWith("#")) tagText else "#$tagText"
        
        if (normalizedTag !in currentTags) {
            currentTags.add(normalizedTag)
            viewModel.updateTags(currentTags)
        }
    }
    
    private fun removeTag(tag: String) {
        val currentTags = (viewModel.uiState.value as? AddEditMemoryUiState.Editing)
            ?.memory?.tags?.toMutableList() ?: return
            
        currentTags.remove(tag)
        viewModel.updateTags(currentTags)
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Collect UI state
                viewModel.uiState.collect { state ->
                    when (state) {
                        is AddEditMemoryUiState.Loading -> {
                            showLoading(true)
                        }
                        is AddEditMemoryUiState.Editing -> {
                            showLoading(false)
                            updateUI(state.memory)
                        }
                        is AddEditMemoryUiState.Saving -> {
                            showLoading(true)
                        }
                        is AddEditMemoryUiState.Error -> {
                            showLoading(false)
                            showError(state.message)
                        }
                    }
                }
            }
        }
        
        // Collect events
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.events.collectLatest { event ->
                when (event) {
                    is AddEditMemoryViewModel.UiEvent.ShowMessage -> {
                        showSnackbar(event.message)
                    }
                    is AddEditMemoryViewModel.UiEvent.MemorySaved -> {
                        // Navigate back with result
                        findNavController().previousBackStackEntry?.savedStateHandle?.set(
                            "memory_saved",
                            event.memoryId
                        )
                        findNavController().navigateUp()
                    }
                    is AddEditMemoryViewModel.UiEvent.MemoryUpdated -> {
                        // Navigate back with result
                        findNavController().previousBackStackEntry?.savedStateHandle?.set(
                            "memory_updated",
                            true
                        )
                        findNavController().navigateUp()
                    }
                    null -> { /* No-op */ }
                }
            }
        }
    }
    
    private fun updateUI(memory: Memory) {
        // Only update the title if it's different to prevent cursor jumping
        if (binding.titleEditText.text?.toString() != memory.title) {
            binding.titleEditText.setText(memory.title)
        }
        
        // Only update the content if it's different to prevent cursor jumping
        if (binding.contentEditText.text?.toString() != memory.content) {
            binding.contentEditText.setText(memory.content)
        }
        
        // Update pin state
        binding.pinButton.setIconResource(
            if (memory.isPinned) {
                R.drawable.ic_pin_24
            } else {
                R.drawable.ic_pin_outline_24
            }
        )
        
        // Update tags
        updateTagsUI(memory.tags)
    }
    
    private fun updateTagsUI(tags: List<String>) {
        binding.tagsContainer.removeAllViews()
        
        tags.forEach { tag ->
            val chip = Chip(requireContext()).apply {
                text = tag
                isCloseIconVisible = true
                setOnCloseIconClickListener {
                    removeTag(tag)
                }
            }
            binding.tagsContainer.addView(chip)
        }
        
        binding.tagsLabel.isVisible = tags.isNotEmpty()
    }
    
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
    }
    
    private fun showError(message: String) {
        showSnackbar(message, Snackbar.LENGTH_LONG)
    }
    
    private fun showSnackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        binding.root.showSnackbar(message, duration)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private fun androidx.core.widget.doOnTextChanged(
        listener: (text: CharSequence) -> Unit
    ) {
        addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                listener(s?.toString() ?: "")
            }
        })
    }
}
