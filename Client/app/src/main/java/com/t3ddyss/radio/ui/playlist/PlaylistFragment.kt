package com.t3ddyss.radio.ui.playlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.t3ddyss.radio.R
import com.t3ddyss.radio.adapters.CollectionAdapter
import com.t3ddyss.radio.adapters.TracksAdapter
import com.t3ddyss.radio.databinding.FragmentPlaylistBinding
import com.t3ddyss.radio.models.domain.*
import com.t3ddyss.radio.ui.collection.CollectionFragment
import com.t3ddyss.radio.utilities.DEBUG_TAG
import com.t3ddyss.radio.utilities.PLAYLIST_ID

class PlaylistFragment : Fragment() {
    private val viewModel by viewModels<PlaylistViewModel> {
        PlaylistViewModelFactory(requireArguments().getInt(PLAYLIST_ID))
    }

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TracksAdapter
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.listTracks.layoutManager = layoutManager

        subscribeUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun subscribeUi() {
        viewModel.tracks.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Loading -> binding.layoutLoading.isVisible = true

                is Success<List<Track>> -> {
                    binding.layoutLoading.isVisible = false

                    adapter = TracksAdapter(result.content!!) {
                        Toast.makeText(activity?.applicationContext, "Clicked track ${it.title}",
                        Toast.LENGTH_SHORT).show()
                    }
                    binding.listTracks.adapter = adapter

                    (parentFragment as? CollectionFragment)?.hideLoadingIndicator()
                }

                is Error -> {
                    binding.layoutLoading.isVisible = false
                    Toast.makeText(activity?.applicationContext, result.message, Toast.LENGTH_SHORT).show()
                }

                is Failed -> {
                    binding.layoutLoading.isVisible = false
                    Toast.makeText(activity?.applicationContext,
                        getString(R.string.no_connection), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        fun newInstance(playlist: Playlist): PlaylistFragment {
            return PlaylistFragment().apply {
                arguments = Bundle().apply {
                    putInt(PLAYLIST_ID, playlist.id)
                }
            }
        }
    }
}