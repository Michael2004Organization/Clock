package com.example.clock

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clock.Views.AnalogClockComposable
import com.example.clock.Views.Settings
import com.example.clock.Views.TextClockComposable
import kotlinx.coroutines.delay
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Preview
@Composable
fun MainView() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val items = listOf(
                Pair("clock", painterResource(id = R.drawable.uhr)),
                Pair("timeStop", painterResource(id = R.drawable.stoppuhr)),
            )

            BottomNavigationBar(items, navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "clock"
        ) {
            composable("clock") {
                ClockView(navController)
            }

            composable("timeStop") {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(color = Color.Black)
                ) {
                    TimeStopView()
                }
            }

            composable("settings") {
                Settings()
            }
        }
    }
}

//@Preview
@Composable
fun ClockView(
    navController: NavController
) {
    val backgroundColor = colorResource(R.color.appBlack)
    val backgroundColorApp = colorResource(R.color.surfaceDark)

    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 50.dp)
            .background(color = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp, top = 10.dp),
                text = "Clock",
                textAlign = TextAlign.Start,
                color = Color.White,
                fontSize = 24.sp
            )

            IconButton(
                modifier = Modifier,
                onClick = {
                    navController.navigate("settings")
                },
            ) {
                Icon(
                    modifier = Modifier,
                    imageVector = Icons.Default.MoreVert,
                    tint = Color.White,
                    contentDescription = "Settings Icon",
                )
            }
        }

        TextClockComposable()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
                .background(color = backgroundColorApp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnalogClock()
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.35f)
                .padding(bottom = 60.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            CitiesClockInfos()
        }
    }
}

@Composable
fun AnalogClock() {
    var isClockRunning by remember { mutableStateOf(true) }

    AnalogClockComposable(
        modifier = Modifier
            .clickable {
                isClockRunning = !isClockRunning
            },
        isClockRunning = isClockRunning
    )
}

@Composable
fun CitiesClockInfos() {
    val listTimeZones = listOf(
        Pair("New York", "America/New_York"),
        Pair("Hong Kong", "Asia/Hong_Kong"),
        Pair("Berlin", "Europe/Berlin"),
        Pair("London", "Europe/London"),
        Pair("Tokyo", "Asia/Tokyo"),
        Pair("Sydney", "Australia/Sydney"),
    )

    listTimeZones.forEach { city ->
        val time = remember {
            mutableStateOf("")
        }

        LaunchedEffect(Unit) {
            while (true) {
                val now = ZonedDateTime.now(ZoneId.of(city.second))
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                time.value = now.format(formatter)

                val secondsUntilNextMinute = 60 - now.second
                delay(secondsUntilNextMinute * 1000L)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .background(
                    color = colorResource(R.color.cardBackground),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = city.first,
                color = Color(0xFFB0BEC5),
                textAlign = TextAlign.Start,
                fontSize = 18.sp
            )
            Text(
                text = time.value,
                color = Color.White,
                textAlign = TextAlign.End,
                fontSize = 22.sp
            )
        }
    }
}

@Composable
fun TimeStopView() {
    val backgroundColor = colorResource(R.color.appBlack)
    var isRunning by remember { mutableStateOf(false) }
    var elapsedMillis by remember { mutableLongStateOf(0L) }
    var startTime by remember { mutableLongStateOf(0L) }
    var accumulatedMillis by remember { mutableLongStateOf(0L) }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            startTime = System.currentTimeMillis()
            while (isRunning) {
                delay(10L)
                elapsedMillis = accumulatedMillis + (System.currentTimeMillis() - startTime)
            }
        }
    }

    val hours = (elapsedMillis / 3600000).toInt()
    val minutes = ((elapsedMillis % 3600000) / 60000).toInt()
    val seconds = ((elapsedMillis % 60000) / 1000).toInt()
    val centiseconds = ((elapsedMillis % 1000) / 10).toInt()

    val timeText = String.format("%02d:%02d:%02d.%02d", hours, minutes, seconds, centiseconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Stopwatch",
            color = Color(0xFFB0BEC5),
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = timeText,
            color = Color.White,
            fontSize = 48.sp,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Button(
                onClick = {
                    if (isRunning) {
                        isRunning = false
                        accumulatedMillis = elapsedMillis
                    } else {
                        isRunning = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) Color(0xFFFF5252) else Color(0xFF00BCD4)
                ),
                modifier = Modifier.size(100.dp, 48.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = if (isRunning) "Stop" else "Start",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            OutlinedButton(
                onClick = {
                    isRunning = false
                    elapsedMillis = 0L
                    accumulatedMillis = 0L
                },
                border = BorderStroke(1.dp, Color(0xFF607D8B)),
                modifier = Modifier.size(100.dp, 48.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = "Reset",
                    color = Color(0xFF607D8B),
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun BottomNavigationBar(
    navItems: List<Pair<String, Painter>> =
        listOf(
            Pair("clock", painterResource(id = R.drawable.uhr)),
            Pair("timeStop", painterResource(id = R.drawable.stoppuhr)),
        ),
    navController: NavController = rememberNavController()
) {
    val bottomAppBarColor = colorResource(R.color.appBlack)
    val iconHeight = 35.dp
    val iconColor = Color.White

    BottomAppBar(
        modifier = Modifier
            .height(iconHeight * 2),
        containerColor = bottomAppBarColor
    ) {
        Row(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            navItems.forEach { item ->
                IconButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .height(iconHeight)
                        .weight(1f),
                    onClick = {
                        navController.navigate(item.first)
                    }
                ) {
                    Icon(
                        modifier = Modifier
                            .size(iconHeight),
                        tint = iconColor,
                        //imageVector = item.second,
                        painter = item.second,
                        contentDescription = item.first
                    )
                }
            }
        }
    }
}