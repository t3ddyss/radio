package com.t3ddyss.radio.models.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaylistAndTrack(
    val playlistId: Int,
    val trackId: Int
) : Parcelable