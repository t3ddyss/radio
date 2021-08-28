package com.t3ddyss.radio.ui.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.t3ddyss.radio.data.RadioRepository
import com.t3ddyss.radio.models.domain.Loading
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class PlaylistViewModel @AssistedInject constructor(
    private val repository: RadioRepository,
    @Assisted private val playlistId: Int,
) : ViewModel() {

    val tracks = liveData {
        emit(Loading())
        emit(repository.getPlaylistTracks(playlistId))
    }

    @AssistedFactory
    interface PlaylistViewModelFactory {
        fun create(playlistId: Int): PlaylistViewModel
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            assistedFactory: PlaylistViewModelFactory,
            playlistId: Int
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return assistedFactory.create(playlistId) as T
            }
        }
    }
}