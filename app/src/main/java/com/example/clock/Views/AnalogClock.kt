package com.example.clock.Views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.clock.TestModels.ClockData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.LocalTime
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Preview
@Composable
fun AnalogClockComposable(
    modifier: Modifier = Modifier,
    minSize: Dp = 300.dp,
    time: LocalTime = LocalTime.now(),
    isClockRunning: Boolean = true
) {
    var seconds by remember { mutableIntStateOf(time.second) }
    var minutes by remember { mutableIntStateOf(time.minute) }
    var hours by remember { mutableIntStateOf(time.hour) }

    var hourAngle by remember { mutableDoubleStateOf(value = 0.0) }

    // Colors
    val clockRingColor = Color(0xFF64B5F6)
    val tickMarkColor = Color.White
    val numberColor = android.graphics.Color.WHITE
    val centerDotColor = Color(0xFF64B5F6)
    val hourHandColor = Color.White
    val minuteHandColor = Color.White
    val secondHandColor = Color(0xFFEF5350)
    val pausedTextColor = android.graphics.Color.argb(200, 239, 83, 80)

    LaunchedEffect(isClockRunning) {
        if (isClockRunning) {
            withContext(Dispatchers.IO) {
                val currentTime = ClockData.getAtomTime()
                hours = currentTime.hour.toIntOrNull() ?: time.hour
                minutes = currentTime.minute.toIntOrNull() ?: time.minute
                seconds = currentTime.second.toIntOrNull() ?: time.second
            }
        }
    }

    LaunchedEffect(key1 = minutes) {
        hourAngle = (minutes / 60.0 * 30.0) - 90.0 + (hours % 12 * 30)
    }

    LaunchedEffect(isClockRunning) {
        while (isClockRunning) {
            seconds += 1

            if (seconds >= 60) {
                seconds = 0
                minutes++
            }
            if (minutes >= 60) {
                minutes = 0
                hours++
            }

            delay(1000L)
        }
    }

    BoxWithConstraints {
        val width = if (minWidth < 1.dp) minSize else minWidth
        val height = if (minHeight < 1.dp) minSize else minHeight

        Canvas(
            modifier = modifier.size(width, height)
        ) {
            val radius = size.width * .44f

            // Outer ring
            drawCircle(
                color = clockRingColor,
                style = Stroke(width = radius * .04f),
                radius = radius,
                center = size.center
            )

            // Subtle inner ring
            drawCircle(
                color = clockRingColor.copy(alpha = 0.15f),
                style = Stroke(width = radius * .005f),
                radius = radius * .96f,
                center = size.center
            )

            // Tick marks and numbers
            val angleStep = 360f / 60f
            (1..60).forEach { i ->
                val rad = (((angleStep * i) - 90f) * (PI / 180f)).toFloat()
                val isHourMark = i % 5 == 0

                val outerR = radius * .94f
                val innerR = if (isHourMark) radius * .84f else radius * .90f
                val strokeW = if (isHourMark) radius * .025f else radius * .010f

                drawLine(
                    color = if (isHourMark) tickMarkColor else tickMarkColor.copy(alpha = 0.5f),
                    start = Offset(outerR * cos(rad) + size.center.x, outerR * sin(rad) + size.center.y),
                    end = Offset(innerR * cos(rad) + size.center.x, innerR * sin(rad) + size.center.y),
                    strokeWidth = strokeW,
                    cap = StrokeCap.Round
                )

                // Hour numbers (1–12)
                if (isHourMark) {
                    drawContext.canvas.nativeCanvas.apply {
                        val posX = (radius * .73f) * cos(rad) + size.center.x
                        val posY = (radius * .73f) * sin(rad) + size.center.y
                        val text = (i / 5).toString()
                        val paint = android.graphics.Paint().apply {
                            textSize = radius * .14f
                            color = numberColor
                            isAntiAlias = true
                            typeface = android.graphics.Typeface.DEFAULT_BOLD
                        }
                        val rect = android.graphics.Rect()
                        paint.getTextBounds(text, 0, text.length, rect)
                        drawText(text, posX - rect.width() / 2f, posY + rect.height() / 2f, paint)
                    }
                }
            }

            // Hour hand
            drawLine(
                color = hourHandColor,
                start = size.center,
                end = Offset(
                    x = (radius * .52f) * cos((hourAngle * (PI / 180)).toFloat()) + size.center.x,
                    y = (radius * .52f) * sin((hourAngle * (PI / 180)).toFloat()) + size.center.y,
                ),
                strokeWidth = radius * .045f,
                cap = StrokeCap.Round
            )

            // Minute hand
            val minuteAngle = (seconds / 60.0 * 6.0) - 90.0 + (minutes * 6.0)
            drawLine(
                color = minuteHandColor,
                start = size.center,
                end = Offset(
                    x = (radius * .72f) * cos((minuteAngle * (PI / 180)).toFloat()) + size.center.x,
                    y = (radius * .72f) * sin((minuteAngle * (PI / 180)).toFloat()) + size.center.y
                ),
                strokeWidth = radius * .025f,
                cap = StrokeCap.Round
            )

            // Second hand
            drawLine(
                color = secondHandColor,
                start = Offset(
                    x = -(radius * .18f) * cos(seconds.secondsToRad()) + size.center.x,
                    y = -(radius * .18f) * sin(seconds.secondsToRad()) + size.center.y
                ),
                end = Offset(
                    x = (radius * .88f) * cos(seconds.secondsToRad()) + size.center.x,
                    y = (radius * .88f) * sin(seconds.secondsToRad()) + size.center.y
                ),
                strokeWidth = radius * .012f,
                cap = StrokeCap.Round
            )

            // Centre dot
            drawCircle(
                color = secondHandColor,
                radius = radius * .035f,
                center = size.center
            )
            drawCircle(
                color = centerDotColor,
                radius = radius * .015f,
                center = size.center
            )

            // Paused overlay
            if (!isClockRunning) {
                drawContext.canvas.nativeCanvas.apply {
                    val text = "PAUSED"
                    val paint = android.graphics.Paint().apply {
                        textSize = radius * .15f
                        color = pausedTextColor
                        isAntiAlias = true
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                    }
                    val rect = android.graphics.Rect()
                    paint.getTextBounds(text, 0, text.length, rect)
                    drawText(
                        text,
                        size.center.x - rect.width() / 2f,
                        size.center.y + rect.height() / 2f,
                        paint
                    )
                }
            }
        }
    }
}

fun Int.secondsToRad(): Float {
    val angle = (360f / 60f * this) - 90f
    return (angle * (PI / 180f)).toFloat()
}