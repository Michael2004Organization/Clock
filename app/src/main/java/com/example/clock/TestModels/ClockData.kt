package com.example.clock.TestModels

import org.apache.commons.net.ntp.NTPUDPClient
import java.net.InetAddress
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class ClockData {
    data class ClockValues(
        var day: String,
        var month: String,
        var year: String,

        var dayDate: String,

        var hour: String,
        var minute: String,
        var second: String,

        //var date: Date
    )

    companion object {
        fun getAtomTime(): ClockValues {
            val baseDate = getNtpTime() ?: Date()
            return formatTime(
                date = baseDate,
                clockValues = ClockValues("", "", "", "", "", "", "")
            )
        }

        fun getNtpTime(): Date? {
            val timeServer = "time.google.com"

            return try {
                NTPUDPClient().use { client ->
                    client.defaultTimeout = 2000
                    val inetAddress = InetAddress.getByName(timeServer)
                    val timeInfo = client.getTime(inetAddress)
                    Date(timeInfo.message.transmitTimeStamp.time)
                }
            } catch (_: Exception) {
                null
            }
        }

        fun formatTime(
            date: Date, clockValues: ClockValues
        ): ClockValues {
            val locale = Locale.getDefault()
            val zonedDateTime = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())

            clockValues.day = zonedDateTime.format(DateTimeFormatter.ofPattern("EEE", locale))
            clockValues.month = zonedDateTime.format(DateTimeFormatter.ofPattern("MMMM", locale))
            clockValues.year = zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy", locale))
            clockValues.dayDate = zonedDateTime.format(DateTimeFormatter.ofPattern("dd", locale))
            clockValues.hour = zonedDateTime.format(DateTimeFormatter.ofPattern("HH", locale))
            clockValues.minute = zonedDateTime.format(DateTimeFormatter.ofPattern("mm", locale))
            clockValues.second = zonedDateTime.format(DateTimeFormatter.ofPattern("ss", locale))

            return clockValues
        }
    }
}