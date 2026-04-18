package com.example.clock.TestModels

import org.apache.commons.net.ntp.NTPUDPClient
import java.net.InetAddress
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
            val ntpTime = getNtpTime()

            return if (ntpTime != null) {
                val zonedDateTime = ZonedDateTime.ofInstant(ntpTime.toInstant(), ZoneId.systemDefault())
                formatZonedDateTime(zonedDateTime)
            } else {
                // Fallback to device local time when NTP is unavailable
                formatZonedDateTime(ZonedDateTime.now(ZoneId.systemDefault()))
            }
        }

        fun getNtpTime(): Date? {
            val client = NTPUDPClient()
            val timeServer = "time.google.com"
            val inetAddress = InetAddress.getByName(timeServer)

            return try {
                val timeInfo = client.getTime(inetAddress)
                Date(timeInfo.message.transmitTimeStamp.time)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        private fun formatZonedDateTime(zonedDateTime: ZonedDateTime): ClockValues {
            val day = zonedDateTime.format(DateTimeFormatter.ofPattern("EEE"))
            val month = zonedDateTime.format(DateTimeFormatter.ofPattern("MMMM"))
            val year = zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy"))
            val dayDate = zonedDateTime.format(DateTimeFormatter.ofPattern("dd"))

            val hour = zonedDateTime.hour
            val minute = zonedDateTime.minute
            val second = zonedDateTime.second

            return ClockValues(
                day = day,
                month = month,
                year = year,
                dayDate = dayDate,
                hour = if (hour < 10) "0$hour" else hour.toString(),
                minute = if (minute < 10) "0$minute" else minute.toString(),
                second = if (second < 10) "0$second" else second.toString(),
            )
        }

        fun formatTime(date: Date, clockValues: ClockValues): ClockValues {
            val zonedDateTime = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
            val result = formatZonedDateTime(zonedDateTime)
            clockValues.day = result.day
            clockValues.month = result.month
            clockValues.year = result.year
            clockValues.dayDate = result.dayDate
            clockValues.hour = result.hour
            clockValues.minute = result.minute
            clockValues.second = result.second
            return clockValues
        }
    }
}