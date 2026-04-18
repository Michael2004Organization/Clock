package com.example.clock.Views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clock.TestModels.ClockData
import com.example.clock.ui.theme.ClockTheme
import com.example.clock.ui.theme.SoftWhite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

//@Preview
@Composable
fun TextClockComposable(
    modifier: Modifier = Modifier
) {
    var day by remember { mutableStateOf(value = "Mo.,") }
    var month by remember { mutableStateOf(value = "January") }
    var year by remember { mutableStateOf(value = "January") }
    var dayDate by remember { mutableStateOf(value = "10") }

    var hour by remember { mutableStateOf(value = "00") }
    var minute by remember { mutableStateOf(value = "00") }
    var second by remember { mutableStateOf(value = "00") }

    LaunchedEffect(Unit) {
        while (true) {
            val currentTime = withContext(Dispatchers.IO) { ClockData.getAtomTime() }

            day = currentTime.day
            month = currentTime.month
            year = currentTime.year
            dayDate = currentTime.dayDate
            hour = currentTime.hour
            minute = currentTime.minute
            second = currentTime.second

            delay(1000L)
        }
    }

    val textColor = SoftWhite
    val clockPartsSize = 56.sp
    val clockHeight = 67.dp
    val clockAdditionalInfos = 20.sp

    val clockItems = listOf(hour, ":", minute, ":", second)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        Text(
            modifier = Modifier
                .height(clockHeight)
                .fillMaxWidth(),
            text = clockItems.joinToString(separator = ""),
            textAlign = TextAlign.Center,
            color = textColor,
            fontSize = clockPartsSize,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            text = "$day $dayDate. $month $year",
            color = textColor.copy(alpha = 0.9f),
            textAlign = TextAlign.Center,
            fontSize = clockAdditionalInfos,
            fontWeight = FontWeight.Medium
        )
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = "Synchronized with network time when available",
            color = textColor.copy(alpha = 0.75f),
            textAlign = TextAlign.Center,
            fontSize = 12.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ClockTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFF111318))
                .padding(12.dp)
        ) {
            TextClockComposable()
        }
    }
}