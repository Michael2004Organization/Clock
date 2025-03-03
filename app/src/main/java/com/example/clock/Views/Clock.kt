package com.example.clock.Views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.example.clock.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.apache.commons.net.ntp.NTPUDPClient
import java.net.InetAddress
import java.text.SimpleDateFormat
import java.time.Clock
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Preview
@Composable
fun CurrentTimeDisplay() {
    var currentTime by remember { mutableStateOf(value = "Time is Loading...") }

    var dayString by remember { mutableStateOf(value = "Mo.,") }
    var dayDate by remember { mutableStateOf(value = "10") }
    var month by remember { mutableStateOf(value = "January") }

    var hour by remember { mutableStateOf(value = "00") }
    var minute by remember { mutableStateOf(value = "00") }
    var second by remember { mutableStateOf(value = "00") }

    LaunchedEffect(Unit) {
        while (true) {
            withContext(Dispatchers.IO) {
                currentTime = getCurrentTimeAndSetValues()

                getValuesInTriple(currentTime).let { (ds, dd, h, m, s, mth) ->
                    dayString = ds
                    dayDate = dd

                    hour = h
                    minute = m
                    second = s

                    month = mth
                }
            }
            delay(1000)
        }
    }

    val viewBackgroundColor = colorResource(R.color.appBlack)
    val textColor = Color.White
    val clockPartsSize = 65.sp
    val clockHeight = 50.dp

    val clockItems = listOf(hour, ":", minute, ":", second)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(viewBackgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 10.dp, top = 10.dp),
                text = "Uhr",
                textAlign = TextAlign.Start,
                color = textColor,
                fontSize = 28.sp
            )
        }
        Row(
            modifier = Modifier
                .height(clockHeight)
                .weight(0.08f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            clockItems.forEach { item ->
                Text(
                    modifier = Modifier,
                    text = item,
                    textAlign = TextAlign.Start,
                    color = textColor,
                    fontSize = clockPartsSize
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$dayString $dayDate. $month",
                color = textColor,
                fontSize = 20.sp
            )
        }

        Spacer(
            modifier = Modifier
                .weight(0.8f)
        )
    }
}

fun getCurrentTimeAndSetValues(): String {
    val ntpTime = getNtpTime()
    var currentTime = "Loading..."

    currentTime = if (ntpTime != null) {
        formatTime(ntpTime)
    } else {
        "Fehler beim Abrufen der Zeit"
    }

    return currentTime
}

data class ClockValues(
    val dayString: String,
    val dayDate: String,
    val hour: String,
    val minute: String,
    val second: String,
    val month: String,
)

fun getValuesInTriple(
    currentTime: String
): ClockValues {
    val dayString = currentTime.substring(0, 4)

    var dayDate = currentTime.substring(14, 16)
    if (dayDate.substring(0, 1) == "0") {
        dayDate = dayDate.substring(1)
    }

    val month = currentTime.substring(22)

    val hour = currentTime.substring(5, 7)
    val minute = currentTime.substring(8, 10)
    val second = currentTime.substring(11, 13)

    return ClockValues(dayString, dayDate, hour, minute, second, month)
}

fun getNtpTime(): Date? {
    val client = NTPUDPClient()
    //val timeServer = "pool.ntp.org" //time.google.com
    val timeServer = "ptbtime1.ptb.de" //time.google.com
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

fun formatTime(date: Date): String {
    val formatter = SimpleDateFormat("EEE, HH:mm:ss dd yyyy MMMM", Locale.GERMANY)
    formatter.timeZone = TimeZone.getTimeZone("Europe/Berlin")

    return formatter.format(date)
}