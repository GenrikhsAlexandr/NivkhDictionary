package com.aleksandrgenrikhs.nivkhdictionary.presentation.tabs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.aleksandrgenrikhs.nivkhdictionary.databinding.FragmentEnglishBinding
import com.aleksandrgenrikhs.nivkhdictionary.di.ComponentProvider
import com.aleksandrgenrikhs.nivkhdictionary.di.viewModel.MainViewModelFactory
import com.aleksandrgenrikhs.nivkhdictionary.domain.Language
import com.aleksandrgenrikhs.nivkhdictionary.presentation.MainViewModel
import com.aleksandrgenrikhs.nivkhdictionary.presentation.WordDetailsBottomSheet
import com.aleksandrgenrikhs.nivkhdictionary.presentation.adapter.WordAdapter
import kotlinx.coroutines.launch
import javax.inject.Inject

class EnglishFragment : Fragment() {

    companion object {
        fun newInstance() = EnglishFragment()
    }

    @Inject
    lateinit var viewModelFactory: MainViewModelFactory
    private val viewModel: MainViewModel by viewModels { viewModelFactory }
    private var _binding: FragmentEnglishBinding? = null
    private val binding: FragmentEnglishBinding get() = _binding!!
    private val adapter: WordAdapter = WordAdapter(
        onWordClick = { word ->
            viewModel.onWordClicked(word)
            WordDetailsBottomSheet.show(
                fragmentManager = childFragmentManager
            )
        },
        locale = Language.ENGLISH.code
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as ComponentProvider).provideComponent()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnglishBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvWord.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )
        binding.rvWord.adapter = adapter
        subscribe()
    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.words.collect { words ->
                adapter.submitList(words)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isProgressBarVisible.collect {
                binding.progressBar.isVisible = it
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isRvWordVisible.collect {
                binding.rvWord.isVisible = it
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isWordNotFound.collect {
                binding.wordNotFound.isVisible = it
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.searchVisible(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}