package com.t3ddyss.radio.services

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.t3ddyss.radio.R
import com.t3ddyss.radio.models.domain.PlaylistAndTrack
import com.t3ddyss.radio.models.domain.Track
import com.t3ddyss.radio.utilities.*


class AudioPlaybackService : Service() {
    private lateinit var exoPlayer: SimpleExoPlayer
    val player get() = exoPlayer

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
                    val intent = Intent(PLAYING_TRACK_CHANGED).apply {
                        exoPlayer.currentMediaItem?.mediaMetadata?.extras?.let {
                            putExtras(it)
                        }
                    }

                    LocalBroadcastManager
                        .getInstance(this@AudioPlaybackService)
                        .sendBroadcast(intent)
                }

                override fun onPlayerError(error: ExoPlaybackException) {
                    if (error.type == ExoPlaybackException.TYPE_SOURCE &&
                        error.sourceException is HttpDataSource.HttpDataSourceException) {
                        Toast
                            .makeText(applicationContext, R.string.no_connection, Toast.LENGTH_SHORT)
                            .show()
                    }
                    exoPlayer.clearMediaItems()

                    // Extras are null to indicate that no tracks are playing right now
                    LocalBroadcastManager
                        .getInstance(this@AudioPlaybackService)
                        .sendBroadcast(Intent(PLAYING_TRACK_CHANGED))
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

        mediaSession = MediaSessionCompat(applicationContext, this::class.java.simpleName).apply {
            isActive = true
        }

        mediaSessionConnector = MediaSessionConnector(mediaSession).apply {
            setPlayer(exoPlayer)
            setQueueNavigator(object : TimelineQueueNavigator(mediaSession) {
                override fun getMediaDescription(
                    player: Player,
                    windowIndex: Int
                ): MediaDescriptionCompat {
                    val track = player.getMediaItemAt(windowIndex)
                    val extras = Bundle().apply {
                        putString(
                            MediaMetadataCompat.METADATA_KEY_ARTIST,
                            track.mediaMetadata.artist.toString())
                    }

                    return MediaDescriptionCompat.Builder()
                        .setExtras(extras)
                        .setTitle(track.mediaMetadata.title.toString())
                        .build()
                }

            })
        }

        notificationManager = AudioNotificationManager(
            applicationContext,
            AudioNotificationListener(),
            mediaSession
        )
    }

    override fun onBind(intent: Intent?): IBinder {
        return AudioPlaybackServiceBinder()
    }

    override fun onDestroy() {
        notificationManager.hideNotification()

        // Removes event listeners as well
        mediaSession.release()
        exoPlayer.release()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

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
        val service get() = this@AudioPlaybackService
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
        }
    }
}