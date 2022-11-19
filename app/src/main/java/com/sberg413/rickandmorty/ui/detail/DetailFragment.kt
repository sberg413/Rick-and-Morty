package com.sberg413.rickandmorty.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.sberg413.rickandmorty.databinding.DetailFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailFragment : Fragment() {

    private val detailViewModel: DetailViewModel by viewModel()
    private var binding: DetailFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DetailFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // detailViewModel.initWithCharacter(args.character)

        binding?.let {
            it.viewmodel = detailViewModel
            it.lifecycleOwner = viewLifecycleOwner
            it.executePendingBindings()
        }

        lifecycleScope.launchWhenStarted {
            detailViewModel.characterData.collect {
                if (it == null) return@collect
                (requireActivity() as AppCompatActivity).supportActionBar?.title =
                    ("${it.name} Details")
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}