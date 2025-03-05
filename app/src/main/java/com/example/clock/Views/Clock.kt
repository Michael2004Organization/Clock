package com.example.clock.Views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clock.R
import com.example.clock.TestModels.ClockData
import com.example.clock.ui.theme.ClockTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.truncate

//@Preview
@Composable
fun TextClockComposable() {
    var day by remember { mutableStateOf(value = "Mo.,") }
    var month by remember { mutableStateOf(value = "January") }
    var year by remember { mutableStateOf(value = "January") }
    var dayDate by remember { mutableStateOf(value = "10") }

    var hour by remember { mutableStateOf(value = "00") }
    var minute by remember { mutableStateOf(value = "00") }
    var second by remember { mutableStateOf(value = "00") }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        while (true) {
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    val currentTime = ClockData.getAtomTime()

                    day = currentTime.day
                    month = currentTime.month
                    year = currentTime.year
                    dayDate = currentTime.dayDate

                    hour = currentTime.hour
                    minute = currentTime.minute
                    second = currentTime.second
                }
            }
            delay(1000L)
        }
    }

    val textColor = Color.White
    val clockPartsSize = 65.sp
    val clockHeight = 67.dp
    val clockAdditionalInfos = 22.sp

    val clockItems = listOf(hour, ":", minute, ":", second)

    Row(
        modifier = Modifier
            .height(clockHeight)
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
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$day $dayDate. $month",
            color = textColor,
            fontSize = clockAdditionalInfos
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ClockTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(R.color.appBlack))
        ) {
            //TextClockComposable()
        }
    }
}