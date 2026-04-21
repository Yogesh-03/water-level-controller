package com.example.waterlevelcontroller.presentation.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waterlevelcontroller.core.utils.Resource
import com.example.waterlevelcontroller.domain.model.PumpControl
import com.example.waterlevelcontroller.domain.model.Sensor
import com.example.waterlevelcontroller.domain.repository.WaterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: WaterRepository
) : ViewModel() {

    private val _pumpControlState = MutableStateFlow<Resource<PumpControl>>(Resource.Loading())
    val pumpControlState: StateFlow<Resource<PumpControl>> = _pumpControlState.asStateFlow()

    private val _waterLevelState = MutableStateFlow<Resource<Sensor>>(Resource.Loading())
    val waterLevelState: StateFlow<Resource<Sensor>> = _waterLevelState.asStateFlow()

    init {
        observePumpControlState()
        observeWaterLevelState()
    }

    private fun observePumpControlState() {
        viewModelScope.launch {
            repository.observePumpControl().collect {
                _pumpControlState.value = it
            }
        }
    }

    private fun observeWaterLevelState() {
        viewModelScope.launch {
            repository.observeWaterLevels().collect {
                _waterLevelState.value = it
            }
        }
    }

//    fun togglePump(current: String?) {
//        viewModelScope.launch {
//            val newState = if (current == "on") "off" else "on"
//            repository.setPump(newState)
//        }
//    }

//    fun togglePump(current: Boolean) {
//        viewModelScope.launch {
//            repository.setPump(if (current) "off" else "on")
//        }
//    }
}