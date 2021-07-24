package com.t3ddyss.radio.ui.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.t3ddyss.radio.MainApplication
import com.t3ddyss.radio.data.RadioRepository
import com.t3ddyss.radio.models.domain.Loading
import javax.inject.Inject

class CollectionViewModel: ViewModel() {
    @Inject
    lateinit var repository: RadioRepository

    init {
        MainApplication.instance.appComponent.inject(this)
    }

    val playlists = liveData {
        emit(Loading())
        emit(repository.getPlaylists())
    }
}