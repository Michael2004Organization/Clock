package com.example.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.example.clock.ui.theme.DeepNavy
import com.example.clock.ui.theme.OceanBlue
import com.example.clock.ui.theme.SlateBlue
import com.example.clock.ui.theme.SoftWhite
import kotlinx.coroutines.delay
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: Painter,
)

@Preview
@Composable
fun MainView() {
    val navController = rememberNavController()
    val navItems = listOf(
        BottomNavItem(
            route = "clock",
            label = stringResource(R.string.navigation_clock),
            icon = painterResource(id = R.drawable.uhr)
        ),
        BottomNavItem(
            route = "timeStop",
            label = stringResource(R.string.navigation_stopwatch),
            icon = painterResource(id = R.drawable.stoppuhr)
        ),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DeepNavy, OceanBlue, SlateBlue)
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                BottomNavigationBar(navItems = navItems, navController = navController)
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "clock",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("clock") {
                    ClockView(navController)
                }

                composable("timeStop") {
                    TimeStopView()
                }

                composable("settings") {
                    Settings()
                }
            }
        }
    }
}

@Composable
fun ClockView(
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.clock_title),
                    textAlign = TextAlign.Start,
                    color = SoftWhite,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(
                    onClick = {
                        navController.navigate("settings")
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        tint = SoftWhite,
                        contentDescription = stringResource(R.string.settings_title),
                    )
                }
            }
        }

        item {
            TextClockComposable(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .padding(vertical = 18.dp)
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.09f)
                )
            ) {
                AnalogClock()
            }
        }

        item {
            Text(
                text = stringResource(R.string.world_time_title),
                color = SoftWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        item {
            CitiesClockInfos()
        }
    }
}

@Composable
fun AnalogClock() {
    var isClockRunning by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { isClockRunning = !isClockRunning },
        contentAlignment = Alignment.Center
    ) {
        AnalogClockComposable(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            isClockRunning = isClockRunning
        )
    }
}

@Composable
fun CitiesClockInfos() {
    val cityTimeZones = listOf(
        "New York" to "America/New_York",
        "Hong Kong" to "Asia/Hong_Kong",
        "Berlin" to "Europe/Berlin",
        "London" to "Europe/London",
        "Tokyo" to "Asia/Tokyo",
        "Sydney" to "Australia/Sydney",
    )
    val formatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    var now by remember { mutableStateOf(ZonedDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            now = ZonedDateTime.now()
            delay(1000L)
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        cityTimeZones.forEach { (city, timezone) ->
            val localTime = remember(now) {
                now.withZoneSameInstant(ZoneId.of(timezone)).format(formatter)
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.09f)
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = city,
                        color = SoftWhite,
                        fontSize = 19.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = localTime,
                        color = SoftWhite,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun TimeStopView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.12f)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.stoppuhr),
                    contentDescription = stringResource(R.string.navigation_stopwatch),
                    tint = SoftWhite,
                    modifier = Modifier.size(52.dp)
                )
                Text(
                    text = stringResource(R.string.stopwatch_placeholder_title),
                    color = SoftWhite,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(R.string.stopwatch_placeholder_subtitle),
                    color = SoftWhite.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.heightIn(min = 80.dp))
        Text(
            text = stringResource(R.string.stopwatch_tip),
            color = SoftWhite.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
fun BottomNavigationBar(
    navItems: List<BottomNavItem> =
        listOf(
            BottomNavItem("clock", "Clock", painterResource(id = R.drawable.uhr)),
            BottomNavItem("timeStop", "Stopwatch", painterResource(id = R.drawable.stoppuhr)),
        ),
    navController: NavController = rememberNavController()
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White.copy(alpha = 0.12f)
    ) {
        navItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        painter = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(28.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 12.sp
                    )
                }
            )
        }
    }
}
