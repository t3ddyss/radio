package com.t3ddyss.radio.di

import com.t3ddyss.radio.ui.collection.CollectionViewModel
import com.t3ddyss.radio.ui.playlist.PlaylistViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(viewModel: CollectionViewModel)

    fun inject(viewModel: PlaylistViewModel)
}