package com.example.waterlevelcontroller.presentation.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waterlevelcontroller.core.network.NetworkMonitor
import com.example.waterlevelcontroller.core.utils.Resource
import com.example.waterlevelcontroller.domain.model.PumpControl
import com.example.waterlevelcontroller.domain.model.PumpField
import com.example.waterlevelcontroller.domain.model.Sensor
import com.example.waterlevelcontroller.domain.repository.WaterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.distinctUntilChanged

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


    private val _desiredPumpState = MutableStateFlow<Boolean?>(null)

    // 3. The "Syncing" logic
    val isSyncing: Flow<Boolean> = combine(
        pumpControlState,
        _desiredPumpState
    ) { reported, desired ->
        if (desired == null) return@combine false

        val reportedValue = (reported as? Resource.Success)?.data?.pumpState

        // Logic: Is what the hardware says different from what the user wants?
        reportedValue != desired
    }
        .distinctUntilChanged() // 👈 THIS IS CRITICAL
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    init {
        observeData()
    }

    /**
     * Sets up real-time listeners to Firebase.
     * Using collectLatest ensures we don't process stale data if updates are rapid.
     */
    private fun observeData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.observePumpControl().onStart {
                _pumpControlState.value = Resource.Loading()
            }
                .catch { e ->
                    _pumpControlState.value = Resource.Error(e.message ?: "Connection Failed")
                }
                .collectLatest { resource ->
                    _pumpControlState.value = resource
                }
        }

        viewModelScope.launch(Dispatchers.IO) {
            repository.observeWaterLevels().collectLatest { resource ->
                _waterLevelState.value = resource
            }
        }
    }


    private fun <T> performUpdate(field: PumpField, value: T) {
        viewModelScope.launch(Dispatchers.IO) {
            _updateState.value = Resource.Loading()
            val result = repository.updatePumpField(field, value)
            _updateState.value = result
        }
    }

    fun togglePump(isOn: Boolean) {
        viewModelScope.launch(Dispatchers.IO){
            _desiredPumpState.value = isOn
            performUpdate(PumpField.STATE, isOn)
        }
    }

    fun updateMode(mode: String) {
        performUpdate(PumpField.MODE, mode)
    }

    fun updateManualPump(manualPump: String) {
        performUpdate(PumpField.MANUAL_CONTROL, manualPump)
    }
}