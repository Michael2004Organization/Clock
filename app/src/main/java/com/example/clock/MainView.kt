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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.style.TextAlign
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
import com.example.clock.ui.theme.BackgroundDark
import com.example.clock.ui.theme.OnSurfaceMuted
import com.example.clock.ui.theme.SurfaceDark
import com.example.clock.ui.theme.SurfaceVariantDark
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
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "clock"
        ) {
            composable("clock") {
                ClockView(navController, Modifier.padding(paddingValues))
            }

            composable("timeStop") {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(color = BackgroundDark)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClockView(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxSize()
            .background(color = BackgroundDark)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Clock",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Light,
                    fontSize = 22.sp
                )
            },
            actions = {
                IconButton(onClick = { navController.navigate("settings") }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = "Settings"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = BackgroundDark
            )
        )

        TextClockComposable()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.55f)
                .background(color = BackgroundDark),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnalogClock()
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.45f)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 8.dp),
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
        val time = remember { mutableStateOf("--:--") }

        LaunchedEffect(city.second) {
            while (true) {
                val now = ZonedDateTime.now(ZoneId.of(city.second))
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                time.value = now.format(formatter)

    // sleep until the next whole minute, accounting for sub-second offset
                val secondsUntilNextMinute = 60 - now.second
                val nanosInCurrentSecond = now.nano
                val delayMs = (secondsUntilNextMinute * 1000L) - (nanosInCurrentSecond / 1_000_000L)
                delay(delayMs.coerceAtLeast(100L))
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 5.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = city.first,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = time.value,
                    color = AccentBlue,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.End
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
            text = "Stopwatch — coming soon",
            color = OnSurfaceMuted,
            fontSize = 18.sp,
            fontWeight = FontWeight.Light
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
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = SurfaceDark,
        tonalElevation = 0.dp
    ) {
        navItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.first,
                onClick = {
                    navController.navigate(item.first) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        modifier = Modifier.size(26.dp),
                        painter = item.second,
                        contentDescription = item.first
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentBlue,
                    unselectedIconColor = OnSurfaceMuted,
                    indicatorColor = SurfaceVariantDark
                )
            )
        }
    }
}