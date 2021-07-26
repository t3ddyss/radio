package com.t3ddyss.radio.models.domain

sealed class Resource<T>
class Loading<T>(val content: T? = null) : Resource<T>()
class Success<T>(val content: T) : Resource<T>()
class Error<T>(val message: String?) : Resource<T>()
class Failed<T> : Resource<T>()
