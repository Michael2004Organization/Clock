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

    // Colors — modern dark palette
    val clockRingColor = Color(0xFF334155)
    val tickMarkColor = Color(0xFF64748B)
    val majorTickColor = Color(0xFF94A3B8)
    val timeNumberColor = android.graphics.Color.argb(220, 148, 163, 184)
    val centerCircleColor = Color(0xFF58A6FF)
    val hourHandColor = Color(0xFFE2E8F0)
    val minuteHandColor = Color(0xFFE2E8F0)
    val secondHandColor = Color(0xFFF85149)
    val clockPausedTextColor = android.graphics.Color.argb(200, 248, 81, 73)

    LaunchedEffect(isClockRunning) {
        if (isClockRunning) {
            withContext(Dispatchers.IO) {
                val currentTime = ClockData.getAtomTime()
                hours = currentTime.hour.toInt()
                minutes = currentTime.minute.toInt()
                seconds = currentTime.second.toInt()
            }
        }
    }

    LaunchedEffect(key1 = minutes) {
        hourAngle = (minutes / 60.0 * 30.0) - 90.0 + (hours % 12 * 30)
    }

    LaunchedEffect(isClockRunning) {
        while (isClockRunning) {
            delay(1000L)
            seconds += 1
            if (seconds >= 60) {
                seconds = 0
                minutes += 1
            }
            if (minutes >= 60) {
                minutes = 0
                hours += 1
            }
            if (hours >= 24) {
                hours = 0
            }
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

            // Tick marks
            val angleDegreeDifference = (360f / 60f)
            (1..60).forEach { tick ->
                val angleRad =
                    (((angleDegreeDifference * tick) - 90f) * (PI / 180f)).toFloat()

                val isMajor = tick % 5 == 0
                val lineStart = if (isMajor) radius * .85f else radius * .91f
                val lineColor = if (isMajor) majorTickColor else tickMarkColor
                val strokeW = if (isMajor) radius * .02f else radius * .008f

                drawLine(
                    color = lineColor,
                    start = Offset(
                        x = lineStart * cos(angleRad) + size.center.x,
                        y = lineStart * sin(angleRad) + size.center.y
                    ),
                    end = Offset(
                        x = (radius - (radius * .02f)) * cos(angleRad) + size.center.x,
                        y = (radius - (radius * .02f)) * sin(angleRad) + size.center.y
                    ),
                    strokeWidth = strokeW,
                )

                // Hour numbers at major ticks
                if (isMajor) {
                    drawContext.canvas.nativeCanvas.apply {
                        val positionX = (radius * .70f) * cos(angleRad) + size.center.x
                        val positionY = (radius * .70f) * sin(angleRad) + size.center.y

                        val hourLabel = (tick / 5).toString()
                        val paint = android.graphics.Paint().apply {
                            textSize = radius * .13f
                            color = timeNumberColor
                            isAntiAlias = true
                        }

                        val textRect = android.graphics.Rect()
                        paint.getTextBounds(hourLabel, 0, hourLabel.length, textRect)

                        drawText(
                            hourLabel,
                            positionX - (textRect.width() / 2f),
                            positionY + (textRect.height() / 2f),
                            paint
                        )
                    }
                }
            }

            // Hour hand
            drawLine(
                color = hourHandColor,
                start = size.center,
                end = Offset(
                    x = (radius * .50f) * cos((hourAngle * (PI / 180)).toFloat()) + size.center.x,
                    y = (radius * .50f) * sin((hourAngle * (PI / 180)).toFloat()) + size.center.y,
                ),
                strokeWidth = radius * .04f,
                cap = StrokeCap.Round
            )

            // Minute hand
            val minutesAngle = (seconds / 60.0 * 6.0) - 90.0 + (minutes * 6.0)
            drawLine(
                color = minuteHandColor,
                start = size.center,
                end = Offset(
                    x = (radius * .70f) * cos((minutesAngle * (PI / 180)).toFloat()) + size.center.x,
                    y = (radius * .70f) * sin((minutesAngle * (PI / 180)).toFloat()) + size.center.y
                ),
                strokeWidth = radius * .025f,
                cap = StrokeCap.Round
            )

            // Second hand
            drawLine(
                color = secondHandColor,
                start = Offset(
                    x = -(radius * .15f) * cos(seconds.secondsToRad()) + size.center.x,
                    y = -(radius * .15f) * sin(seconds.secondsToRad()) + size.center.y
                ),
                end = Offset(
                    x = (radius * .85f) * cos(seconds.secondsToRad()) + size.center.x,
                    y = (radius * .85f) * sin(seconds.secondsToRad()) + size.center.y
                ),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Center dot
            drawCircle(
                color = centerCircleColor,
                radius = radius * .04f,
                center = size.center
            )
            drawCircle(
                color = Color(0xFF0D1117),
                radius = radius * .02f,
                center = size.center
            )

            // Paused overlay text
            if (!isClockRunning) {
                drawContext.canvas.nativeCanvas.apply {
                    val text = "PAUSED"
                    val paint = android.graphics.Paint().apply {
                        textSize = radius * .14f
                        color = clockPausedTextColor
                        isAntiAlias = true
                    }
                    val textRect = android.graphics.Rect()
                    paint.getTextBounds(text, 0, text.length, textRect)
                    drawText(
                        text,
                        size.center.x - (textRect.width() / 2f),
                        size.center.y + radius * .55f,
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