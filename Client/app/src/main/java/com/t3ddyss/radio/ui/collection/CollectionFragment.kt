package com.t3ddyss.radio.ui.collection

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.google.android.material.tabs.TabLayoutMediator
import com.t3ddyss.radio.MainApplication
import com.t3ddyss.radio.R
import com.t3ddyss.radio.adapters.CollectionAdapter
import com.t3ddyss.radio.databinding.FragmentCollectionBinding
import com.t3ddyss.radio.models.domain.*
import javax.inject.Inject

class CollectionFragment : Fragment() {
    private val viewModel by viewModels<CollectionViewModel> { viewModelFactory }
    @Inject
    lateinit var viewModelFactory: CollectionViewModelFactory

    private var _binding: FragmentCollectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CollectionAdapter

    override fun onAttach(context: Context) {
        MainApplication.instance.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subscribeUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun hideLoadingIndicator() {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            binding.layoutLoading.isVisible = false
        }
    }

    private fun subscribeUi() {
        viewModel.playlists.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Loading -> binding.layoutLoading.isVisible = true

                is Success<List<Playlist>> -> {
                    adapter = CollectionAdapter(this, result.content)

                    binding.tabLayout.isVisible = true
                    binding.viewPager.adapter = adapter
                    TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                        tab.text = result.content[position].title
                    }.attach()
                }

                is Error -> {
                    binding.layoutLoading.isVisible = false
                    Toast.makeText(
                        activity?.applicationContext,
                        result.message ?: getString(R.string.unknown_error),
                        Toast.LENGTH_SHORT)
                        .show()
                }

                is Failed -> {
                    binding.layoutLoading.isVisible = false
                    Toast.makeText(activity?.applicationContext, getString(R.string.no_connection), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        fun newInstance() = CollectionFragment()
    }
}