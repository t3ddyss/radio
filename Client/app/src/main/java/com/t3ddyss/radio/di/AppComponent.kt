package com.t3ddyss.radio.di

import com.t3ddyss.radio.ui.collection.CollectionFragment
import com.t3ddyss.radio.ui.playlist.PlaylistFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(fragment: CollectionFragment)

    fun inject(fragment: PlaylistFragment)
}