package com.example.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.clock.Views.AnalogClockComposable
import com.example.clock.Views.Settings
import com.example.clock.Views.TextClockComposable
import com.example.clock.ui.theme.AccentBlue
import com.example.clock.ui.theme.DeepBackground
import com.example.clock.ui.theme.SurfaceBackground
import com.example.clock.ui.theme.TextPrimary
import com.example.clock.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Preview
@Composable
fun MainView() {
    val navController = rememberNavController()

    Scaffold(
        containerColor = DeepBackground,
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
                ClockView(navController, modifier = Modifier.padding(paddingValues))
            }

            composable("timeStop") {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(color = DeepBackground)
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

@Composable
fun ClockView(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxSize()
            .background(color = DeepBackground)
            .padding(top = 40.dp)
    ) {
        // Title bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Clock",
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 2.sp
            )

            IconButton(onClick = { navController.navigate("settings") }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    tint = TextSecondary,
                    contentDescription = "Settings"
                )
            }
        }

        // Digital clock
        TextClockComposable()

        // Analog clock
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
                .background(color = SurfaceBackground),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnalogClock()
        }

        // World clocks
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f)
                .padding(bottom = 8.dp)
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
        modifier = Modifier.clickable { isClockRunning = !isClockRunning },
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
        val time = remember { mutableStateOf("") }
        val offset = remember { mutableStateOf("") }

        LaunchedEffect(city.second) {
            while (true) {
                val now = ZonedDateTime.now(ZoneId.of(city.second))
                time.value = now.format(DateTimeFormatter.ofPattern("HH:mm"))
                offset.value = now.format(DateTimeFormatter.ofPattern("z"))

                // Sync to next minute boundary
                val secondsLeft = 60 - now.second
                delay(secondsLeft * 1000L)
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 5.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = city.first,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = offset.value,
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
                Text(
                    text = time.value,
                    color = AccentBlue,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}

@Composable
fun TimeStopView() {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Stopwatch",
            color = TextPrimary,
            fontSize = 22.sp
        )
    }
}

@Preview
@Composable
fun BottomNavigationBar(
    navItems: List<Pair<String, Painter>> = listOf(
        Pair("clock", painterResource(id = R.drawable.uhr)),
        Pair("timeStop", painterResource(id = R.drawable.stoppuhr)),
    ),
    navController: NavController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar(
        containerColor = SurfaceBackground,
        contentColor = AccentBlue,
        tonalElevation = 0.dp
    ) {
        navItems.forEach { item ->
            val selected = currentRoute == item.first
            NavigationBarItem(
                selected = selected,
                onClick = { navController.navigate(item.first) },
                icon = {
                    Icon(
                        modifier = Modifier.size(26.dp),
                        painter = item.second,
                        contentDescription = item.first,
                    )
                },
                label = {
                    Text(
                        text = if (item.first == "clock") "Clock" else "Stopwatch",
                        fontSize = 12.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentBlue,
                    selectedTextColor = AccentBlue,
                    indicatorColor = AccentBlue.copy(alpha = 0.15f),
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                )
            )
        }
    }
}