package com.t3ddyss.radio.models.mappers

import com.t3ddyss.radio.models.domain.Track
import com.t3ddyss.radio.models.dto.TrackDto

fun mapTrackDtoToTDomain(trackDto: TrackDto): Track {
    return Track(
        id = trackDto.id,
        artist = trackDto.artist,
        title = trackDto.title,
        length = trackDto.length,
        url = trackDto.url
    )
}