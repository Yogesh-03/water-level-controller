package com.example.waterlevelcontroller.presentation.ui.screens.schedule

import android.util.Log
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _ScheduleState = MutableStateFlow<Resource<List<Schedule>>>(Resource.Loading())
    val ScheduleState: StateFlow<Resource<List<Schedule>>> = _ScheduleState.asStateFlow()

    private val _addScheduleState = MutableStateFlow<Resource<Unit>>(Resource.Success(Unit))
    val addScheduleState = _addScheduleState.asStateFlow()

    val isOnline = networkMonitor.isConnected

    init {
        observeSchedule()
    }

    fun updateSchedule(data: Schedule) {
        viewModelScope.launch(Dispatchers.IO) {

        }
    }

    fun addSchedule(schedule: Schedule){
        viewModelScope.launch(Dispatchers.IO) {
            scheduleRepository.addSchedule(schedule, "Yogesh", "Yogesh Yadav")
        }
    }

    fun observeSchedule(){
        viewModelScope.launch {
            scheduleRepository.observeSchedule()
                .onStart {
                    _ScheduleState.value = Resource.Loading()
                }
                .collectLatest { resource ->
                    _ScheduleState.value = resource
                    Log.d("Schedule", resource.data.toString())
                }
        }
    }

     fun deleteSchedule(scheduleId : String){
        viewModelScope.launch(Dispatchers.IO) {
            scheduleRepository.deleteSchedule(scheduleId)
        }
    }
}