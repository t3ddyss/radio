package com.t3ddyss.radio.models.domain

data class Track(
    val id: Int,
    val artist: String,
    val title: String,
    val length: String,
    val url: String,
    var isPlaying: Boolean = false
)