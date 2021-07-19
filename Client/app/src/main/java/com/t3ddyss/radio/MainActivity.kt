package com.t3ddyss.radio

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.t3ddyss.radio.databinding.ActivityMainBinding
import com.t3ddyss.radio.ui.collection.CollectionFragment


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.host_fragment, CollectionFragment.newInstance())
                .commit()
        }
    }
}