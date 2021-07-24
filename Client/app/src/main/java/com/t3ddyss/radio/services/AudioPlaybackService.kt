package com.t3ddyss.radio.services

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
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

        exoPlayer = SimpleExoPlayer.Builder(this).build().apply {
//            addListener(object : Player.Listener {
//                override fun onTracksChanged(
//                    trackGroups: TrackGroupArray,
//                    trackSelections: TrackSelectionArray
//                ) {
//                    for (i in 0 until trackGroups.length) {
//                        val trackGroup = trackGroups.get(i)
//                        for (j in 0 until trackGroup.length) {
//                            val metadata: Metadata? = trackGroup.getFormat(j).metadata
//                            if (metadata != null) {
//                                for (n in 0 until metadata.length()) {
//                                    when (val md = metadata[n]) {
//                                        is com.google.android.exoplayer2.metadata.id3.TextInformationFrame -> {
//                                        }
//                                        else -> {
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            })

            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true)

            setHandleAudioBecomingNoisy(true)

            setMediaItem(MediaItem.Builder()
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setArtist("Dollkraut")
                        .setTitle("Mastermaster")
                        .build()
                )
                .setUri(getBaseUrlForCurrentDevice() + "api/tracks/Dollkraut_Mastermaster_29.mp3")
                .build())
            pauseAtEndOfMediaItems

            playWhenReady = true
            prepare()
        }

        mediaSession = MediaSessionCompat(this, "AudioPlaybackService").apply {
            isActive = true
        }

        notificationManager = AudioNotificationManager(
            this,
            AudioNotificationListener(),
            mediaSession
        )

        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlayer(exoPlayer)

        notificationManager.showNotification(exoPlayer)
    }

    override fun onBind(intent: Intent?): IBinder {
        return AudioPlaybackServiceBinder()
    }

    override fun onDestroy() {
        serviceScope.cancel()
        notificationManager.hideNotification()
        exoPlayer.release()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    inner class AudioPlaybackServiceBinder: Binder() {
        fun getExoPlayerInstance() = exoPlayer
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