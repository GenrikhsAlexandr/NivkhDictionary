package com.aleksandrgenrikhs.nivkhdictionary.presentation.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.aleksandrgenrikhs.nivkhdictionary.databinding.FragmentNivkhBinding
import com.aleksandrgenrikhs.nivkhdictionary.presentation.WordAdapter
import com.aleksandrgenrikhs.nivkhdictionary.presentation.WordDetailsBottomSheet
import kotlinx.coroutines.launch

class NivkhFragment : Fragment() {

    private val viewModel: TabsViewModel by viewModels()


    companion object {
        fun newInstance() = NivkhFragment()
    }

    private var _binding: FragmentNivkhBinding? = null
    private val binding: FragmentNivkhBinding get() = _binding!!

    private val adapter: WordAdapter = WordAdapter(
        onWordClick = { word ->
            WordDetailsBottomSheet.show(
                word, fragmentManager = childFragmentManager
            )
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNivkhBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvWord.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        binding.rvWord.adapter = adapter


        lifecycleScope.launch {
            viewModel.words.collect { words ->
                binding.progressBar.isVisible = words.isEmpty()
                binding.rvWord.isVisible = words.isNotEmpty()
                adapter.submitData(words)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}