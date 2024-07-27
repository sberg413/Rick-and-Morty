package com.sberg413.rickandmorty.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainFragment : Fragment() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent{
                MaterialTheme {
                    MainCharacterListScreen(viewModel = mainViewModel)
                }
            }
        }
    }

    @OptIn(FlowPreview::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
        requireActivity().addMenuProvider(
            StatusMenuProvider(mainViewModel),
            viewLifecycleOwner,
            Lifecycle.State.STARTED
        )

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.characterClicked
                    .filterNotNull()
                    .collect {
                        val action = MainFragmentDirections.actionShowDetailFragment(it.id)
                        Log.d(TAG, "characterClicked: $it | action: $action")
                        findNavController().navigate(action)
                        mainViewModel.updateStateWithCharacterClicked(null)
                }
            }
        }
    }


    companion object {
        private const val TAG = "MainFragment"

        @Suppress("UNUSED")
        fun newInstance() = MainFragment()
    }

}