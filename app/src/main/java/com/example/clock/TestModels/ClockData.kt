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
            }

            return currentTime
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
//                val formatter = SimpleDateFormat(pattern.first, Locale.GERMANY)
//                formatter.timeZone = TimeZone.getTimeZone("Europe/Berlin")
//                val time = formatter.format(date)
//                time = formatter.format(Date(formatter.parse(time)!!.time - 8 * 1000))

                //Auto
                val zonedDateTime =
                    ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())

                val formatter = DateTimeFormatter.ofPattern(pattern.first)
                val result = zonedDateTime.format(formatter)

                if (pattern.second == 1) {
                    clockValues.day = result
                } else if (pattern.second == 2) {
                    clockValues.month = result
                } else if (pattern.second == 3) {
                    clockValues.year = result
                } else if (pattern.second == 4) {
                    clockValues.dayDate = result
                } else if (pattern.second == 5) {
                    val updatedZonedDateTime = zonedDateTime.plusHours(1).minusSeconds(8)
                    val time = updatedZonedDateTime.format(formatter)

                    val currentTime =
                        LocalTime.parse(time, DateTimeFormatter.ofPattern(pattern.first))

                    clockValues.hour =
                        if (currentTime.hour < 10) "0${currentTime.hour}" else currentTime.hour.toString()
                    clockValues.minute =
                        if (currentTime.minute < 10) "0${currentTime.minute}" else currentTime.minute.toString()
                    clockValues.second =
                        if (currentTime.second < 10) "0${currentTime.second}" else currentTime.second.toString()

//                    clockValues.date =
//                        Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant())
                }
            }

            return clockValues
        }
    }
}