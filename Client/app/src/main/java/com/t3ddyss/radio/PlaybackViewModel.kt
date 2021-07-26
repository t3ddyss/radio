package com.t3ddyss.radio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.t3ddyss.radio.models.domain.PlaylistAndTrack

class PlaybackViewModel : ViewModel() {
    private val _currentlyPlayingTrack = MutableLiveData<PlaylistAndTrack?>()
    val currentlyPlayingTrack: LiveData<PlaylistAndTrack?> = _currentlyPlayingTrack

    fun updateCurrentlyPlayingTrack(playlistAndTrack: PlaylistAndTrack?) {
        _currentlyPlayingTrack.value = playlistAndTrack
    }
}