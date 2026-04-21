package com.example.waterlevelcontroller.core.extensions

import com.example.waterlevelcontroller.core.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

// Generic extension to wrap Flow<T> into Flow<Resource<T>>
fun <T> Flow<T>.asResource(): Flow<Resource<T>> {
    return this
        .map<T, Resource<T>> { data ->
            Resource.Success(data)
        }
        .catch { exception ->
            emit(Resource.Error<T>(exception.message ?: "Unknown error"))
        }
}