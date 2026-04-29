package com.example.waterlevelcontroller.core.constants

object FirebasePaths {

    const val HUMIDITY = "sensors/humidity"
    const val TEMPERATURE = "sensors/temperature"
    const val OH_LOW = "sensors/overheadLow"
    const val OH_HIGH = "sensors/overheadHigh"

    const val UG_LOW = "sensors/undergroundLow"
    const val UG_HIGH = "sensors/undergroundHigh"

    //    const val MANUAL_PUMP = "manualPump"
    const val MANUAL_PUMP_DESIRED = "controls/manualPump/desired"
    const val MANUAL_PUMP_REPORTED = "controls/manualPump/reported"
    const val MODE_DESIRED = "controls/mode/desired"
    const val MODE_REPORTED = "controls/mode/reported"
    const val PUMP_STATE_DESIRED = "controls/pumpState/desired"
    const val PUMP_STATE_REPORTED = "controls/pumpState/reported"

}