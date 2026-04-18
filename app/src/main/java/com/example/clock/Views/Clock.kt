package com.example.clock.Views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clock.R
import com.example.clock.TestModels.ClockData
import com.example.clock.ui.theme.ClockTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

//@Preview
@Composable
fun TextClockComposable() {
    var day by remember { mutableStateOf(value = "Mo.,") }
    var month by remember { mutableStateOf(value = "January") }
    var dayDate by remember { mutableStateOf(value = "10") }

    var hour by remember { mutableStateOf(value = "00") }
    var minute by remember { mutableStateOf(value = "00") }
    var second by remember { mutableStateOf(value = "00") }

    LaunchedEffect(Unit) {
        while (true) {
            val currentTime = withContext(Dispatchers.IO) {
                ClockData.getAtomTime()
            }

            day = currentTime.day
            month = currentTime.month
            dayDate = currentTime.dayDate

            hour = currentTime.hour
            minute = currentTime.minute
            second = currentTime.second

            delay(1000L)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = hour,
                color = Color.White,
                fontSize = 72.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Light,
                letterSpacing = 2.sp
            )
            Text(
                text = ":",
                color = Color(0xFF6C63FF),
                fontSize = 72.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
            Text(
                text = minute,
                color = Color.White,
                fontSize = 72.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Light,
                letterSpacing = 2.sp
            )
            Text(
                text = ":",
                color = Color(0xFF6C63FF),
                fontSize = 72.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
            Text(
                text = second,
                color = Color(0xFFAAAAAA),
                fontSize = 72.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Light,
                letterSpacing = 2.sp
            )
        }
        Text(
            text = "$day $dayDate. $month",
            color = Color(0xFF888888),
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
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