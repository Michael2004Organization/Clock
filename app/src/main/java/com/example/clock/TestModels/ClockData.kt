package com.example.clock.TestModels

import org.apache.commons.net.ntp.NTPUDPClient
import java.net.InetAddress
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

class ClockData {
    data class ClockValues(
        var day: String,
        var month: String,
        var year: String,

        var dayDate: String,

        var hour: String,
        var minute: String,
        var second: String,
    )

    companion object {
        fun getAtomTime(): ClockValues {
            // Fall back to device time if NTP is unavailable
            val ntpTime = getNtpTime() ?: Date()

            return formatTime(ntpTime, ClockValues("", "", "", "", "", "", ""))
        }

        fun getNtpTime(): Date? {
            val client = NTPUDPClient()
            val timeServer = "time.google.com"
            return try {
                val inetAddress = InetAddress.getByName(timeServer)
                val timeInfo = client.getTime(inetAddress)
                Date(timeInfo.message.transmitTimeStamp.time)
            } catch (e: Exception) {
                null
            }
        }

        fun formatTime(
            date: Date, clockValues: ClockValues
        ): ClockValues {
            val zonedDateTime = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())

            val patterns = listOf(
                Pair("EEE", 1),
                Pair("MMMM", 2),
                Pair("yyyy", 3),
                Pair("dd", 4),
                Pair("HH:mm:ss", 5),
            )

            patterns.forEach { pattern ->
                val formatter = DateTimeFormatter.ofPattern(pattern.first)

                when (pattern.second) {
                    1 -> clockValues.day = zonedDateTime.format(formatter)
                    2 -> clockValues.month = zonedDateTime.format(formatter)
                    3 -> clockValues.year = zonedDateTime.format(formatter)
                    4 -> clockValues.dayDate = zonedDateTime.format(formatter)
                    5 -> {
                        val currentTime = LocalTime.of(
                            zonedDateTime.hour, zonedDateTime.minute, zonedDateTime.second
                        )
                        clockValues.hour =
                            if (currentTime.hour < 10) "0${currentTime.hour}" else currentTime.hour.toString()
                        clockValues.minute =
                            if (currentTime.minute < 10) "0${currentTime.minute}" else currentTime.minute.toString()
                        clockValues.second =
                            if (currentTime.second < 10) "0${currentTime.second}" else currentTime.second.toString()
                    }
                }
            }

            return clockValues
        }
    }
}