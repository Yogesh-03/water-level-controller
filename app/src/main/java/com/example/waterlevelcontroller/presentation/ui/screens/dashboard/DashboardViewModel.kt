package com.example.waterlevelcontroller.presentation.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waterlevelcontroller.core.network.NetworkMonitor
import com.example.waterlevelcontroller.core.utils.Resource
import com.example.waterlevelcontroller.domain.model.PumpControl
import com.example.waterlevelcontroller.domain.model.Sensor
import com.example.waterlevelcontroller.domain.repository.WaterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: WaterRepository,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    val isOnline = networkMonitor.isConnected

    // Real-time state of the Pump (Mode, State, Manual overrides)
    private val _pumpControlState = MutableStateFlow<Resource<PumpControl>>(Resource.Loading())
    val pumpControlState: StateFlow<Resource<PumpControl>> = _pumpControlState.asStateFlow()

    // Real-time state of the Tank Sensors (Low/High levels)
    private val _waterLevelState = MutableStateFlow<Resource<Sensor>>(Resource.Loading())
    val waterLevelState: StateFlow<Resource<Sensor>> = _waterLevelState.asStateFlow()

    private val _updateState = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val updateState = _updateState.asStateFlow()

    // Tracks if an update is currently pending (to disable buttons or show a small loader)
    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating.asStateFlow()

    init {
        observeData()
    }

    /**
     * Sets up real-time listeners to Firebase.
     * Using collectLatest ensures we don't process stale data if updates are rapid.
     */
    private fun observeData() {
        viewModelScope.launch {
            repository.observePumpControl().collectLatest { resource ->
                _pumpControlState.value = resource
            }
        }

        viewModelScope.launch {
            repository.observeWaterLevels().collectLatest { resource ->
                _waterLevelState.value = resource
            }
        }
    }

    /**
     * Sends new data to the Repository.
     * We don't update local state manually; we let the Firebase listener handle it.
     */
    fun updatePumpControl(data: PumpControl)  {
        viewModelScope.launch(Dispatchers.IO) {
            _isUpdating.value = true
            val result = repository.updatePumpControl(data)
            _updateState.value = result

            // If there's an error, you might want to trigger a UI event (like a Snackbar)
            if (result is Resource.Error) {
                // Log or handle error
            }

            _isUpdating.value = false
        }
    }

    /**
     * Helper to toggle pump state without needing the UI to construct the full object.
     */
    fun togglePump(isOn: Boolean)   {
        viewModelScope.launch {
            val currentData = (pumpControlState.value as? Resource.Success)?.data
            currentData?.let {
                updatePumpControl(it.copy(pumpState = isOn))
                if (currentData.mode == "auto" && isOn) {
                   // updateManualPump("on")
                }
            }
        }
    }

    /**
     * Helper to change the operation mode (e.g., "Auto", "Manual")
     */
    fun updateMode(mode: String) {
        val currentData = (pumpControlState.value as? Resource.Success)?.data
        currentData?.let {
            updatePumpControl(it.copy(mode = mode))

        }
    }

    fun updateManualPump(manualPump: String){
        val currentData = (pumpControlState.value as? Resource.Success)?.data
        currentData?.let {
            updatePumpControl(it.copy(manualPump = manualPump))
        }

    }
}