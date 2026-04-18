package com.example.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.milliseconds

private val BackgroundColor = Color(0xFF0A0A0A)
private val SurfaceColor = Color(0xFF1A1A1A)
private val CardColor = Color(0xFF1E1E2E)
private val AccentColor = Color(0xFF6C63FF)

data class NavItem(val route: String, val label: String, val icon: ImageVector)

@Preview
@Composable
fun MainView() {
    val navController = rememberNavController()

    val navItems = listOf(
        NavItem("clock", "Clock", Icons.Filled.Schedule),
        NavItem("timeStop", "Stopwatch", Icons.Filled.Timer),
    )

    Scaffold(
        containerColor = BackgroundColor,
        bottomBar = {
            BottomNavigationBar(navItems, navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "clock"
        ) {
            composable("clock") {
                ClockView(navController, paddingValues)
            }

            composable("timeStop") {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(color = BackgroundColor)
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
    paddingValues: PaddingValues = PaddingValues()
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(color = BackgroundColor)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Clock",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
            },
            actions = {
                IconButton(
                    onClick = { navController.navigate("settings") }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        tint = Color.White,
                        contentDescription = "Settings"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = BackgroundColor
            )
        )

        TextClockComposable()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
                .background(color = BackgroundColor),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnalogClock()
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.35f)
                .padding(bottom = paddingValues.calculateBottomPadding())
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

                    val formatter = DateTimeFormatter.ofPattern("HH:mm")

                    time.value = ZonedDateTime
                        .now(ZoneId.of(city.second))
                        .format(formatter)

                    delayTime -= (currentTime.second * 1000).milliseconds
                }

                delay(delayTime)
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = city.first,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = time.value,
                    color = AccentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    textAlign = TextAlign.End
                )
            }
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
            text = "Stopwatch",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Light
        )
    }
}

@Preview
@Composable
fun BottomNavigationBar(
    navItems: List<NavItem> = listOf(
        NavItem("clock", "Clock", Icons.Filled.Schedule),
        NavItem("timeStop", "Stopwatch", Icons.Filled.Timer),
    ),
    navController: NavController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = SurfaceColor,
        tonalElevation = 0.dp
    ) {
        navItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 12.sp
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentColor,
                    selectedTextColor = AccentColor,
                    indicatorColor = Color(0xFF2A2A3E),
                    unselectedIconColor = Color(0xFF888888),
                    unselectedTextColor = Color(0xFF888888)
                )
            )
        }
    }
}