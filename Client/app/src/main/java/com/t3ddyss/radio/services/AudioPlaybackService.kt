package com.t3ddyss.radio.services

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.t3ddyss.radio.models.domain.PlaylistAndTrack
import com.t3ddyss.radio.models.domain.Track
import com.t3ddyss.radio.utilities.*
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
            addListener(object : Player.Listener {
                override fun onTracksChanged(
                    trackGroups: TrackGroupArray,
                    trackSelections: TrackSelectionArray
                ) {
                    exoPlayer.currentMediaItem?.mediaMetadata?.extras?.let {
                        val intent = Intent(PLAYING_TRACK_CHANGED).apply {
                            putExtras(it)
                        }

                        LocalBroadcastManager
                            .getInstance(this@AudioPlaybackService)
                            .sendBroadcast(intent)
                    }
                }
            })
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true)
            setHandleAudioBecomingNoisy(true)
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = true
        }

        mediaSession = MediaSessionCompat(applicationContext, "AudioPlaybackService").apply {
            isActive = true
        }

        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlayer(exoPlayer)

        notificationManager = AudioNotificationManager(
            this,
            AudioNotificationListener(),
            mediaSession
        )
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

    fun getExoPlayer() = exoPlayer

    fun setTracksAndPlay(tracks: List<Track>, startIndex: Int, playlistId: Int) {
        exoPlayer.setMediaItems(
            tracks.map {
                MediaItem.Builder()
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setArtist(it.artist)
                            .setTitle(it.title)
                            .setExtras(
                                Bundle().apply {
                                    putParcelable(
                                        PLAYLIST_AND_TRACK,
                                        PlaylistAndTrack(
                                            playlistId = playlistId,
                                            trackId = it.id)
                                    )
                                }
                            )
                            .build()
                    )
                    .setMediaId(it.id.toString())
                    .setUri(it.url)
                    .build()
            },
            startIndex,
            C.TIME_UNSET
        )

        exoPlayer.prepare()
        notificationManager.showNotification(exoPlayer)
    }

    inner class AudioPlaybackServiceBinder: Binder() {
        fun getService() = this@AudioPlaybackService
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