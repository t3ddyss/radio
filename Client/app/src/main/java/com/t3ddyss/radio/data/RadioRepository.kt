package com.t3ddyss.radio.data

import com.t3ddyss.radio.api.RadioService
import com.t3ddyss.radio.models.domain.Playlist
import com.t3ddyss.radio.models.domain.Resource
import com.t3ddyss.radio.models.domain.Success
import com.t3ddyss.radio.models.domain.Track
import com.t3ddyss.radio.models.mappers.mapPlaylistDtoToDomain
import com.t3ddyss.radio.models.mappers.mapTrackDtoToTDomain
import com.t3ddyss.radio.utilities.handleNetworkException
import javax.inject.Inject

class RadioRepository @Inject constructor(
    private val service: RadioService
) {
    suspend fun getPlaylists(): Resource<List<Playlist>> {
        return handleNetworkException {
            Success(service.getPlaylists().map { mapPlaylistDtoToDomain(it) })
        }
    }

    suspend fun getPlaylistTracks(playlistId: Int): Resource<List<Track>> {
        return handleNetworkException {
            Success(service.getPlaylistTracks(playlistId).map { mapTrackDtoToTDomain(it) })
        }
    }
}