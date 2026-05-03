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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TargetState(
    val pumpState: Boolean? = null,
    val mode: String? = null,
    val manualPump: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: WaterRepository,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    val isOnline = networkMonitor.isConnected

    private val _pumpControlState = MutableStateFlow<Resource<PumpControl>>(Resource.Loading())
    val pumpControlState: StateFlow<Resource<PumpControl>> = _pumpControlState.asStateFlow()

    private val _waterLevelState = MutableStateFlow<Resource<Sensor>>(Resource.Loading())
    val waterLevelState: StateFlow<Resource<Sensor>> = _waterLevelState.asStateFlow()

    private val _updateState = MutableStateFlow<Resource<Unit>>(Resource.Success(Unit))
    val updateState = _updateState.asStateFlow()

    private val _targetState = MutableStateFlow(TargetState())

    private var syncTimeoutJob: Job? = null

    // To handle database rollback, we store the previous valid state here
    private var lastValidState: PumpControl? = null

    val isSyncing: Flow<Boolean> = combine(
        pumpControlState,
        _targetState
    ) { reported, target ->
        val data = (reported as? Resource.Success)?.data ?: return@combine false

        // Update our reference to the last known good state from the server
        lastValidState = data

        val stateMismatch = target.pumpState != null && data.pumpState != target.pumpState
        val modeMismatch = target.mode != null && data.mode != target.mode
        val manualMismatch = target.manualPump != null && data.manualPump != target.manualPump

        stateMismatch || modeMismatch || manualMismatch
    }.distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        observeData()
        observeSyncCompletion()
    }

    private fun observeSyncCompletion() {
        viewModelScope.launch {
            isSyncing.collect { syncing ->
                if (!syncing) {
                    syncTimeoutJob?.cancel()
                    syncTimeoutJob = null
                    _targetState.value = TargetState()
                }
            }
        }
    }

    private fun observeData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.observePumpControl()
                .onStart { _pumpControlState.value = Resource.Loading() }
                .catch { e -> _pumpControlState.value = Resource.Error(e.message ?: "Connection Failed") }
                .collectLatest { resource -> _pumpControlState.value = resource }
        }

        viewModelScope.launch(Dispatchers.IO) {
            repository.observeWaterLevels().collectLatest { resource ->
                _waterLevelState.value = resource
            }
        }
    }

    /**
     * Updated to handle database rollback on timeout
     */
    private fun <T> performUpdate(field: PumpField, value: T) {
        syncTimeoutJob?.cancel()

        // Capture the value currently in the database before we change it
        val rollbackValue: Any? = when(field) {
            PumpField.STATE -> lastValidState?.pumpState
            PumpField.MODE -> lastValidState?.mode
            PumpField.MANUAL_CONTROL -> lastValidState?.manualPump
        }

        viewModelScope.launch(Dispatchers.IO) {
            _updateState.value = Resource.Loading()
            val result = repository.updatePumpField(field, value)
            _updateState.value = result

            if (result is Resource.Error) {
                _targetState.value = TargetState()
            } else {
                // Pass the rollback info to the timeout handler
                startSyncTimeout(field, rollbackValue)
            }
        }
    }

    private fun startSyncTimeout(field: PumpField, rollbackValue: Any?) {
        syncTimeoutJob = viewModelScope.launch {
            delay(10000)

            if (isSyncing.first()) {
                // 1. Revert the UI state
                _targetState.value = TargetState()

                // 2. REVERT THE DATABASE: Write the old value back to Firebase
                if (rollbackValue != null) {
                    repository.updatePumpField(field, rollbackValue)
                }

                _updateState.value = Resource.Error("Hardware Timeout: Reverting database state")
            }
        }
    }

    fun togglePump(isOn: Boolean) {
        _targetState.value = _targetState.value.copy(pumpState = isOn)
        performUpdate(PumpField.STATE, isOn)
    }

    fun updateMode(mode: String) {
        _targetState.value = _targetState.value.copy(mode = mode)
        performUpdate(PumpField.MODE, mode)
    }

    fun updateManualPump(manualPump: String) {
        _targetState.value = _targetState.value.copy(manualPump = manualPump)
        performUpdate(PumpField.MANUAL_CONTROL, manualPump)
    }
}