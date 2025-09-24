package com.example.persianaiapp.ui.memories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.persianaiapp.R
import com.example.persianaiapp.data.model.Memory
import com.example.persianaiapp.ui.memories.compose.MemoriesScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MemoriesFragment : Fragment() {

    private val viewModel: MemoriesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MemoriesScreen(
                    viewModel = viewModel,
                    onMemoryClick = { memory ->
                        // Navigate to memory detail
                        findNavController().navigate(
                            MemoriesFragmentDirections.actionMemoriesToMemoryDetail(memory.id)
                        )
                    },
                    onAddMemoryClick = {
                        findNavController().navigate(
                            MemoriesFragmentDirections.actionMemoriesToAddMemory()
                        )
                    }
                )
            }
        }
    }
}
