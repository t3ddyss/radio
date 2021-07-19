package com.t3ddyss.radio

import android.app.Application
import com.t3ddyss.radio.di.AppComponent
import com.t3ddyss.radio.di.DaggerAppComponent

class MainApplication : Application() {
    val appComponent: AppComponent = DaggerAppComponent.create()

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: MainApplication
    }
}