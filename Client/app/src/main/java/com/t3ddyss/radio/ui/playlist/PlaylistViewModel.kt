package com.t3ddyss.radio.ui.playlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.t3ddyss.radio.MainApplication
import com.t3ddyss.radio.data.RadioRepository
import com.t3ddyss.radio.models.domain.Loading
import com.t3ddyss.radio.utilities.DEBUG_TAG
import javax.inject.Inject

class PlaylistViewModel(
    private val playlistId: Int
) : ViewModel() {

    @Inject
    lateinit var repository: RadioRepository

    init {
        MainApplication.instance.appComponent.inject(this)
    }

    val tracks = liveData {
        emit(Loading())
        emit(repository.getPlaylistTracks(playlistId))
    }
}