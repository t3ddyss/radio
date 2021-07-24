package com.t3ddyss.radio

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.t3ddyss.radio.databinding.ActivityMainBinding
import com.t3ddyss.radio.services.AudioPlaybackService
import com.t3ddyss.radio.ui.collection.CollectionFragment
import com.t3ddyss.radio.utilities.DEBUG_TAG
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(DEBUG_TAG, "Service connected")

            if (service is AudioPlaybackService.AudioPlaybackServiceBinder) {
                binding.playerControlView.player = service.getExoPlayerInstance()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(DEBUG_TAG, "Service disconnected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Radio)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.host_fragment, CollectionFragment.newInstance())
                .commit()
        }

        bindService(
            Intent(this, AudioPlaybackService::class.java),
            connection,
            Context.BIND_AUTO_CREATE
        )
    }
}