package com.t3ddyss.radio.api

import com.t3ddyss.radio.models.dto.PlaylistDto
import com.t3ddyss.radio.models.dto.TrackDto
import retrofit2.http.GET
import retrofit2.http.Path

interface RadioService {
    @GET("api/playlists/")
    suspend fun getPlaylists(): List<PlaylistDto>

    @GET("api/playlists/{playlist_id}")
    suspend fun getPlaylistTracks(
        @Path("playlist_id") playlistId: Int
    ): List<TrackDto>
}