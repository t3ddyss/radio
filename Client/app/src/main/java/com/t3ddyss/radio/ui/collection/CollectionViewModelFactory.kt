package com.t3ddyss.radio.ui.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.t3ddyss.radio.data.RadioRepository
import java.lang.IllegalArgumentException
import javax.inject.Inject

class CollectionViewModelFactory @Inject constructor(
    private val repository: RadioRepository
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CollectionViewModel::class.java)) {
            return CollectionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}