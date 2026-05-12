package com.example.waterlevelcontroller.core.constants

object FirebasePaths {

    // EXACT PATHS
    const val HUMIDITY = "sensors/environment/humidity"
    const val TEMPERATURE = "sensors/environment/temperature"
    const val OH_LOW = "sensors/overhead/low"
    const val OH_HIGH = "sensors/overhead/high"
    const val UG_LOW = "sensors/underground/low"
    const val UG_HIGH = "sensors/underground/high"
    const val MANUAL_PUMP_DESIRED = "controls/manualPump/desired"
    const val MANUAL_PUMP_REPORTED = "controls/manualPump/reported"
    const val MODE_DESIRED = "controls/mode/desired"
    const val MODE_REPORTED = "controls/mode/reported"
    const val PUMP_STATE_DESIRED = "controls/pumpState/desired"
    const val PUMP_STATE_REPORTED = "controls/pumpState/reported"


    // PARTIAL PATHS
    const val SENSORS = "sensors"
    const val PUMP_CONTROLS = "controls"

    const val SENSORS_HUMIDITY = "environment/humidity"
    const val SENSORS_TEMPERATURE = "environment/temperature"
    const val SENSORS_OH_LOW = "overhead/low"
    const val SENSORS_OH_HIGH = "overhead/high"
    const val SENSORS_UG_LOW = "underground/low"
    const val SENSORS_UG_HIGH = "underground/high"
    const val PUMP_CONTROLS_MANUAL_PUMP_DESIRED = "manualPump/desired"
    const val PUMP_CONTROLS_MANUAL_PUMP_REPORTED = "manualPump/reported"
    const val PUMP_CONTROLS_MODE_DESIRED = "mode/desired"
    const val PUMP_CONTROLS_MODE_REPORTED = "mode/reported"
    const val PUMP_CONTROLS_PUMP_STATE_DESIRED = "pumpState/desired"
    const val PUMP_CONTROLS_PUMP_STATE_REPORTED = "pumpState/reported"


    //**************** SCHEDULES PATH **********************

    const val SCHEDULES = "schedules"

    // ************** FIRESTORE PATHS *****************
    const val PUMP_LOGS = "pump_logs"

}