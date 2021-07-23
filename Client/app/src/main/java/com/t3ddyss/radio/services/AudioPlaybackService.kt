package com.t3ddyss.radio.services

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.t3ddyss.radio.utilities.AudioNotificationManager
import com.t3ddyss.radio.utilities.getBaseUrlForCurrentDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class AudioPlaybackService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private lateinit var exoPlayer: SimpleExoPlayer

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private lateinit var notificationManager: AudioNotificationManager

    override fun onCreate() {
        super.onCreate()

        exoPlayer = SimpleExoPlayer.Builder(this).build()
        exoPlayer.setMediaItem(MediaItem.fromUri(getBaseUrlForCurrentDevice() +
                "api/tracks/Dollkraut_Mastermaster_29.mp3"))
        exoPlayer.playWhenReady = true
        exoPlayer.prepare()

        mediaSession = MediaSessionCompat(this, "AudioPlaybackService").apply {
            isActive = true
        }

        notificationManager = AudioNotificationManager(
            this,
            AudioNotificationListener(),
            mediaSession.sessionToken
        )

        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlayer(exoPlayer)

        notificationManager.showNotification(exoPlayer)
    }

    override fun onBind(intent: Intent?): IBinder {
        exoPlayer.playWhenReady = true
        return AudioPlaybackServiceBinder()
    }

    inner class AudioPlaybackServiceBinder: Binder() {
        fun getExoPlayerInstance() = exoPlayer
    }

    override fun onDestroy() {
        serviceScope.cancel()
    }

    inner class AudioNotificationListener : PlayerNotificationManager.NotificationListener {
        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {
            startForeground(notificationId, notification)
        }

        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            stopForeground(true)
            stopSelf()
        }
    }
}