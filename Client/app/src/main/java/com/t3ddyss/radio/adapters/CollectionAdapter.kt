package com.t3ddyss.radio.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.t3ddyss.radio.models.domain.Playlist
import com.t3ddyss.radio.ui.playlist.PlaylistFragment

class CollectionAdapter(
    fragment: Fragment,
    private val playlists: List<Playlist>
) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = playlists.size

    override fun createFragment(position: Int): Fragment {
        return PlaylistFragment.newInstance(playlists[position])
    }
}