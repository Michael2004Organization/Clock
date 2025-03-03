package com.example.clock

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MainView(navController: NavController) {
    Scaffold(
        bottomBar = {
            val items = listOf(
                Pair("Home", Icons.Filled.Home),
                Pair("Home", Icons.Filled.Email),
                Pair("Home", Icons.Filled.Info),
            )

            BottomNavigationBar(items)
        }
    ) { paddingValues ->
        Column(
            Modifier.padding(paddingValues)
        ) {
            Text(text = "Test")
        }
    }
}

@Preview
@Composable
fun BottomNavigationBar(
    navItems: List<Pair<String, ImageVector>> =
        listOf(
            Pair("Home", Icons.Filled.Home),
            Pair("Home", Icons.Filled.Email),
            Pair("Home", Icons.Filled.Info),
        ),
    //navController: NavController
) {
    val iconHeight = 35.dp

    BottomAppBar(
        modifier = Modifier
            .height(iconHeight * 2),
        containerColor = Color.Red
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
                    onClick = {}
                ) {
                    Icon(
                        modifier = Modifier
                            .size(iconHeight),
                        imageVector = item.second,
                        contentDescription = item.first
                    )
                }
            }
        }
    }
}