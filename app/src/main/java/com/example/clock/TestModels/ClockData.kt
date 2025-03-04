package com.example.clock.TestModels

import org.apache.commons.net.ntp.NTPUDPClient
import java.net.InetAddress
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
            val ntpTime = getNtpTime()

            var currentTime = ClockValues(
                "", "", "", "",
                "", "", ""
            )

            if (ntpTime != null) {
                currentTime = formatTime(ntpTime, currentTime)
            } else {
            }

            return currentTime
        }

        fun getTimeValues(
            currentTime: String,
            isAllData: Boolean
        ): ClockValues {
            if (isAllData) {
                val day = currentTime.substring(0, 4)

                val month = currentTime.substring(22)

                val year = ""

                var dayDate = currentTime.substring(14, 16)
                if (dayDate.substring(0, 1) == "0") {
                    dayDate = dayDate.substring(1)
                }

                val hour = currentTime.substring(5, 7)
                val minute = currentTime.substring(8, 10)
                val second = currentTime.substring(11, 13)

                return ClockValues(day, month, year, dayDate, hour, minute, second)
            } else {
                val hour = currentTime.substring(0, 2)
                val minute = currentTime.substring(3, 5)
                val second = currentTime.substring(6, 8)

                return ClockValues("", "", "", "", hour, minute, second)
            }
        }

        fun getNtpTime(): Date? {
            val client = NTPUDPClient()
            //val timeServer = "pool.ntp.org"
            //val timeServer = "ptbtime1.ptb.de"
            val timeServer = "time.google.com"
            val inetAddress = InetAddress.getByName(timeServer)

            try {
                //Anfrage an den NTP-Server
                val timeInfo = client.getTime(inetAddress)

                //Empfange Zeitstempel und konvertiere ihn in ein Date-Objekt
                return Date(timeInfo.message.transmitTimeStamp.time)

            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        fun formatTime(
            date: Date, clockValues: ClockValues
        ): ClockValues {
            val patterns = listOf(
                Pair("EEE", 1),
                Pair("MMMM", 2),
                Pair("yyyy", 3),
                Pair("dd", 4),
                Pair("HH:mm:ss", 5),
            )

            patterns.forEach { pattern ->
                val formatter = SimpleDateFormat(pattern.first, Locale.GERMANY)
                formatter.timeZone = TimeZone.getTimeZone("Europe/Berlin")

                if (pattern.second == 1) {
                    clockValues.day = formatter.format(date)
                } else if (pattern.second == 2) {
                    clockValues.month = formatter.format(date)
                } else if (pattern.second == 3) {
                    clockValues.year = formatter.format(date)
                } else if (pattern.second == 4) {
                    clockValues.dayDate = formatter.format(date)
                } else if (pattern.second == 5) {
                    var time = formatter.format(date)
                    time = formatter.format(Date(formatter.parse(time)!!.time - 8 * 1000))

                    var currentTime =
                        LocalTime.parse(time, DateTimeFormatter.ofPattern(pattern.first))

                    clockValues.hour = currentTime.hour.toString()
                    clockValues.minute = currentTime.minute.toString()
                    clockValues.second = (currentTime.second).toString()

//                    clockValues.date =
//                        Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant())
                }
            }

//            val formatter = SimpleDateFormat("pattern", Locale.GERMANY)
//            formatter.timeZone = TimeZone.getTimeZone("Europe/Berlin")
//
//            return formatter.format(date)

            return clockValues
        }
    }
}