package com.t3ddyss.radio

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.t3ddyss.radio.databinding.ActivityMainBinding
import com.t3ddyss.radio.utilities.DEBUG_TAG
import com.t3ddyss.radio.utilities.getBaseUrlForCurrentDevice
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            val url = getBaseUrlForCurrentDevice() + "api/audios/"

            val mp = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                prepare()
                start()
            }

            mp.setOnPreparedListener {
                Log.d(DEBUG_TAG, "Audio duration is ${it.duration / 1000} seconds")

                updateProgressBar(it, binding.progressBar)
            }

            mp.setOnBufferingUpdateListener { _, percent ->
                Log.d(DEBUG_TAG, "Loaded $percent% from server")

                binding.progressBar.secondaryProgress = percent
            }
        }
    }

    private fun updateProgressBar(mp: MediaPlayer, progressBar: ProgressBar) {
        val duration: Int = mp.duration
        val amountToUpdate = duration / 100
        val timer = Timer()

        timer.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    progressBar.incrementProgressBy(1)
                }
            }
        }, 0, amountToUpdate.toLong())
    }
}