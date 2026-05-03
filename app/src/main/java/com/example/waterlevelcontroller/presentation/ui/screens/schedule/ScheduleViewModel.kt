package com.example.waterlevelcontroller.presentation.ui.screens.schedule

import androidx.annotation.Discouraged
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waterlevelcontroller.core.network.NetworkMonitor
import com.example.waterlevelcontroller.core.utils.Resource
import com.example.waterlevelcontroller.domain.model.Schedule
import com.example.waterlevelcontroller.domain.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val shceRepository: ScheduleRepository,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _ScheduleState = MutableStateFlow<Resource<Schedule>>(Resource.Loading())
    val ScheduleState: StateFlow<Resource<Schedule>> = _ScheduleState.asStateFlow()

    val isOnline = networkMonitor.isConnected

    init {

    }

    fun updateSchedule(data: Schedule) {
        viewModelScope.launch(Dispatchers.IO) {

        }
    }
}