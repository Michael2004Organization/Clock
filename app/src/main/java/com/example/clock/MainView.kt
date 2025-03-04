package com.example.clock

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
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clock.Views.AnalogClockComposable
import com.example.clock.Views.TextClockComposable

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
        val viewBackgroundColor = colorResource(R.color.appBlack)

        NavHost(
            navController = navController,
            startDestination = "clock"
        ) {
            composable("clock") {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(viewBackgroundColor)
                ) {
                    ClockView()
                }
            }

            composable("timeStop") {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(viewBackgroundColor)
                ) {
                    TimeStopView()
                }
            }
        }
    }
}

@Preview
@Composable
fun ClockView() {
    TextClockComposable()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
            .background(color = Color.White),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var isClockRunning by remember { mutableStateOf(true) }

        AnalogClockComposable(
            modifier = Modifier
                .clickable {
                    isClockRunning = !isClockRunning
                },
            isClockRunning = isClockRunning
        )
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