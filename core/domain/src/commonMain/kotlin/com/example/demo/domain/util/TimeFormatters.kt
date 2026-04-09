package com.example.demo.domain.util

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

fun formatSaveTimestamp(epochMillis: Long, timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    if (epochMillis <= 0L) return "Never"
    val dt = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(timeZone)
    val year = dt.year
    val month = dt.month.number.toString().padStart(2, '0')
    val day = dt.day.toString().padStart(2, '0')
    val hour = dt.hour.toString().padStart(2, '0')
    val minute = dt.minute.toString().padStart(2, '0')
    return "$year-$month-$day $hour:$minute"
}