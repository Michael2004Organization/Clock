package com.example.clock.Views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clock.TestModels.ClockData
import com.example.clock.ui.theme.ClockTheme
import com.example.clock.ui.theme.OnSurfaceMuted
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun TextClockComposable() {
    var day by remember { mutableStateOf(value = "Mon") }
    var month by remember { mutableStateOf(value = "January") }
    var dayDate by remember { mutableStateOf(value = "1") }

    var hour by remember { mutableStateOf(value = "00") }
    var minute by remember { mutableStateOf(value = "00") }
    var second by remember { mutableStateOf(value = "00") }

    LaunchedEffect(Unit) {
        while (true) {
            withContext(Dispatchers.IO) {
                val currentTime = ClockData.getAtomTime()

                day = currentTime.day
                month = currentTime.month
                dayDate = currentTime.dayDate

                hour = currentTime.hour
                minute = currentTime.minute
                second = currentTime.second
            }
            delay(1000L)
        }
    }

    val textColor = MaterialTheme.colorScheme.onBackground

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "$hour:$minute",
                color = textColor,
                fontSize = 72.sp,
                fontWeight = FontWeight.Thin,
                letterSpacing = 4.sp
            )
            Text(
                text = ":$second",
                color = OnSurfaceMuted,
                fontSize = 36.sp,
                fontWeight = FontWeight.Thin,
                modifier = Modifier.padding(bottom = 10.dp, start = 2.dp)
            )
        }
        Text(
            text = "$day, $dayDate $month",
            color = OnSurfaceMuted,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
fun ClockPreview() {
    ClockTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            TextClockComposable()
        }
    }
}