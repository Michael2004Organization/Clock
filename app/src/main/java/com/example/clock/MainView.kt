package com.example.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.milliseconds

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
    val backgroundColor = Color.Black
    val backgroundColorApp = colorResource(R.color.appBlack)

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
                text = "Uhr",
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

    val coroutineScope = rememberCoroutineScope()

    listTimeZones.forEach { city ->
        val time = remember {
            mutableStateOf("")
        }

        var delayTime = (60000L).milliseconds

        LaunchedEffect(Unit) {
            while (true) {
                if (delayTime != (60000).milliseconds) {
                    delayTime = (60000).milliseconds
                }

                coroutineScope.launch {
                    val currentTime = ZonedDateTime
                        .now(ZoneId.of(city.second))
                        .plusSeconds(7)

                    val formatter = DateTimeFormatter.ofPattern("HH:mm")

                    time.value = ZonedDateTime
                        .now(ZoneId.of(city.second))
                        .plusSeconds(7)
                        .format(formatter)

                    delayTime -= (currentTime.second * 1000).milliseconds
                }

                delay(delayTime)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .border(1.dp, Color.White, CircleShape),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 12.dp),
                text = "${city.first}: ",
                color = Color.White,
                textAlign = TextAlign.Start,
                fontSize = 28.sp
            )
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp),
                text = time.value,
                color = Color.White,
                textAlign = TextAlign.End,
                fontSize = 28.sp
            )
        }
    }
}

@Composable
fun TimeStopView() {
    Row(
        modifier = Modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "TimeStop View",
            color = Color.White
        )
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
    val bottomAppBarColor = Color.Black
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