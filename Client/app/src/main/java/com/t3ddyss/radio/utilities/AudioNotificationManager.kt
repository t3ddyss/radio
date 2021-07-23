package com.t3ddyss.radio.utilities

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.support.v4.media.session.MediaSessionCompat
import com.google.android.exoplayer2.DefaultControlDispatcher
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.t3ddyss.radio.R
import com.t3ddyss.radio.services.AudioPlaybackService

const val NOW_PLAYING_CHANNEL_ID = "com.t3ddyss.radio.media.NOW_PLAYING"
const val NOW_PLAYING_NOTIFICATION_ID = 1337 // Random number

class AudioNotificationManager(
    context: Context,
    notificationListener: AudioPlaybackService.AudioNotificationListener,
    sessionToken: MediaSessionCompat.Token
) {
    private val notificationManager= PlayerNotificationManager
        .Builder(
            context,
            NOW_PLAYING_NOTIFICATION_ID,
            NOW_PLAYING_CHANNEL_ID,
            AudioDescriptionAdapter())
        .setChannelNameResourceId(R.string.notification_channel)
        .setChannelDescriptionResourceId(R.string.notification_channel_description)
        .setNotificationListener(notificationListener)
        .build().apply {
            setMediaSessionToken(sessionToken)
            setSmallIcon(R.drawable.ic_audiotrack)
            setControlDispatcher(DefaultControlDispatcher(0, 0))
        }

    fun hideNotification() {
        notificationManager.setPlayer(null)
    }

    fun showNotification(player: Player){
        notificationManager.setPlayer(player)
    }

    private inner class AudioDescriptionAdapter : PlayerNotificationManager.MediaDescriptionAdapter {

        override fun createCurrentContentIntent(player: Player): PendingIntent? = null

        override fun getCurrentContentText(player: Player) = "Context text"

        override fun getCurrentContentTitle(player: Player) = "Content title"

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? = null
    }
}
