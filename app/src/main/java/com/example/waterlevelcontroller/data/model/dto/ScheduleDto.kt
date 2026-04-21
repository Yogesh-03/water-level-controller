package com.example.waterlevelcontroller.data.model.dto


data class ScheduleDto(
    val id: String? = null,
    var title: String = "",
    var startTime: String = "",
    var endTime: String? = null,
    var days: List<String> = emptyList(),
    var isEnabled: Boolean = true,
    var untilFull: Boolean = false,
    var duration: String = ""
) {
    // Required by Firestore
    constructor() : this(null, "", "", null, emptyList(), true, false, "")
}