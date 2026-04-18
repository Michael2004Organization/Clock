package com.example.clock.Views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SettingsBackground = Color(0xFF0A0A0A)
private val SettingsAccent = Color(0xFF6C63FF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings() {
    var showAnalogClock by remember { mutableStateOf(true) }
    var showWorldClock by remember { mutableStateOf(true) }
    var use24HourFormat by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = SettingsBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SettingsBackground
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(color = SettingsBackground)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "DISPLAY",
                color = Color(0xFF888888),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            SettingsItem(
                title = "Show Analog Clock",
                subtitle = "Display the analog clock face",
                checked = showAnalogClock,
                onCheckedChange = { showAnalogClock = it }
            )

            HorizontalDivider(color = Color(0xFF2A2A2A), thickness = 1.dp)

            SettingsItem(
                title = "Show World Clock",
                subtitle = "Display world time zones",
                checked = showWorldClock,
                onCheckedChange = { showWorldClock = it }
            )

            HorizontalDivider(color = Color(0xFF2A2A2A), thickness = 1.dp)

            SettingsItem(
                title = "24-Hour Format",
                subtitle = "Use 24-hour time display",
                checked = use24HourFormat,
                onCheckedChange = { use24HourFormat = it }
            )
        }
    }
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
            Text(
                text = subtitle,
                color = Color(0xFF888888),
                fontSize = 13.sp
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = SettingsAccent,
                uncheckedThumbColor = Color(0xFF888888),
                uncheckedTrackColor = Color(0xFF2A2A2A)
            )
        )
    }
}