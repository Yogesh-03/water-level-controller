package com.example.waterlevelcontroller.presentation.ui.screens.history

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waterlevelcontroller.core.network.NetworkMonitor
import com.example.waterlevelcontroller.core.utils.Resource
import com.example.waterlevelcontroller.domain.model.PumpControl
import com.example.waterlevelcontroller.domain.model.PumpLogs
import com.example.waterlevelcontroller.domain.repository.LogsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryScreenViewModel @Inject constructor(
    private val logsRepository: LogsRepository,
    networkMonitor: NetworkMonitor
)  : ViewModel(){
    val isOnline = networkMonitor.isConnected

    private val _logsState = MutableStateFlow<Resource<List<PumpLogs>>>(Resource.Loading())
    val logsState: StateFlow<Resource<List<PumpLogs>>> = _logsState.asStateFlow()

    init {
        getPumpLogs()
    }

     fun getPumpLogs(){
        viewModelScope.launch {
            logsRepository.getPumpLogs()
                .onStart {
                    _logsState.value = Resource.Loading()
                }
                .collectLatest { resource ->
                    _logsState.value = resource
                    Log.d("LOGS", resource.data.toString())
                }
        }
    }

    fun generateFakeYearlyData(): List<Pair<Double, Double>> {
        val random = java.util.Random()
        return (1..365).map { day ->
            // Most days have 10-40 mins of runtime, some days have spikes up to 200 mins
            val baseRuntime = if (random.nextFloat() > 0.95) {
                random.nextInt(150) + 50 // Spike day
            } else {
                random.nextInt(30) + 10 // Normal day
            }
            Pair(day.toDouble(), baseRuntime.toDouble())
        }
    }

}