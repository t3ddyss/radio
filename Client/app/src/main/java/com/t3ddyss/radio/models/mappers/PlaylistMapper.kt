package com.t3ddyss.radio.models.mappers

import com.t3ddyss.radio.models.domain.Playlist
import com.t3ddyss.radio.models.dto.PlaylistDto

fun mapPlaylistDtoToDomain(playlistDto: PlaylistDto): Playlist {
    return Playlist(id = playlistDto.id, title = playlistDto.title)
}