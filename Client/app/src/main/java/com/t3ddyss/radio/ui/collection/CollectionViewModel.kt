package com.t3ddyss.radio.ui.collection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.t3ddyss.radio.MainApplication
import com.t3ddyss.radio.data.RadioRepository
import com.t3ddyss.radio.models.domain.Loading
import com.t3ddyss.radio.models.domain.Playlist
import com.t3ddyss.radio.models.domain.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

class CollectionViewModel: ViewModel() {
    @Inject
    lateinit var repository: RadioRepository

    private val _playlists = MutableLiveData<Resource<List<Playlist>>>()
    val playlists: LiveData<Resource<List<Playlist>>> = _playlists

    init {
        MainApplication.instance.appComponent.inject(this)

        viewModelScope.launch {
            _playlists.postValue(Loading())
            _playlists.postValue(repository.getPlaylists())
        }
    }
}