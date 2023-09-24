package com.sberg413.rickandmorty.ui.main

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.RecyclerView.SCROLL_INDICATOR_TOP
import com.sberg413.rickandmorty.R
import com.sberg413.rickandmorty.adapters.CharacterAdapter
import com.sberg413.rickandmorty.adapters.CharacterLoadStateAdapter
import com.sberg413.rickandmorty.databinding.MainFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class MainFragment : Fragment() {

    private val mainViewModel: MainViewModel by viewModels()
    private var binding: MainFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MainFragmentBinding.inflate(
            inflater, container, false)
        setHasOptionsMenu(true)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val characterAdapter = CharacterAdapter { character ->
            mainViewModel.updateStateWithCharacterClicked(character)
        }
        val divider = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        val header = CharacterLoadStateAdapter { characterAdapter.retry() }
        val footer = CharacterLoadStateAdapter { characterAdapter.retry() }

        binding?.apply {

            recyclerMain.adapter = characterAdapter.withLoadStateFooter(
               // header = header,
                footer = footer
            )
            recyclerMain.addItemDecoration(divider)
            lifecycleOwner = viewLifecycleOwner
            swiperefresh.setOnRefreshListener {
                characterAdapter.refresh()
            }

            searchBar.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    recyclerMain.scrollToPosition(0)
                    mainViewModel.setSearchFilter(searchBar.text.trim().toString())
                    true
                } else {
                    false
                }
            }
            searchBar.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    recyclerMain.scrollToPosition(0)
                    mainViewModel.setSearchFilter(searchBar.text.trim().toString())
                    true
                } else {
                    false
                }
            }

            retryButton.setOnClickListener { characterAdapter.retry() }

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    mainViewModel.listData.collectLatest { pagingData ->
                        Log.d(TAG, "collectLatest = $pagingData")
                        characterAdapter.submitData( pagingData)
                        binding?.swiperefresh?.isRefreshing = false
                    }
                }
            }

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    mainViewModel.characterClicked.collectLatest {
                        if (it != null) {
                            val action = MainFragmentDirections.actionShowDetailFragment(it)
                            findNavController().navigate(action)
                            mainViewModel.updateStateWithCharacterClicked(null)
                        }
                    }
                }
            }

            lifecycleScope.launch {
                characterAdapter.loadStateFlow.collectLatest { loadState ->
                    val isListEmpty = loadState.refresh is LoadState.NotLoading
                            && characterAdapter.itemCount == 0
                    // show empty list
                    emptyList.isVisible = isListEmpty
                    // Only show the list if refresh succeeds.
                    recyclerMain.isVisible  = loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
                    // Show loading spinner during initial load or refresh.
                    progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                    // Show the retry state if initial load or refresh fails.
                    retryButton.isVisible = loadState.source.refresh is LoadState.Error && characterAdapter.itemCount == 0

                    // Toast on any error
                    val errorState = loadState.source.append as? LoadState.Error
                        ?: loadState.source.prepend as? LoadState.Error
                        ?: loadState.append as? LoadState.Error
                        ?: loadState.prepend as? LoadState.Error
                    errorState?.let {
                        Toast.makeText(
                            requireActivity(),
                            "ERROR: ${it.error}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

//                    // Show a retry header if there was an error refreshing, and items were previously
//                    // cached OR default to the default prepend state
//                    header.loadState = loadState.mediator
//                        ?.refresh
//                        ?.takeIf { it is LoadState.Error && characterAdapter.itemCount > 0 }
//                        ?: loadState.prepend
                }
            }
        }
    }

    private val spinnerListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            val filterVal = parent.adapter.getItem(position) as String
            Log.d(TAG," Selected: $filterVal")
            mainViewModel.setSatusFilter(filterVal)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        (menu.findItem(R.id.spinner)?.actionView as Spinner).apply {
            onItemSelectedListener = spinnerListener
            adapter = ArrayAdapter.createFromResource(
                        requireContext(),
                        R.array.filter_options,
                        android.R.layout.simple_spinner_item
                    ).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
            setSelection(
                mainViewModel.getSelectedStatusIndex(
                    resources.getStringArray(R.array.filter_options)
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        private const val TAG = "MainFragment"

        @Suppress("UNUSED")
        fun newInstance() = MainFragment()
    }

}