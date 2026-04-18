package com.example.clock.Views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
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
    var seconds by remember { mutableStateOf(time.second) }
    var minutes by remember { mutableStateOf(time.minute) }
    var hours by remember { mutableStateOf(time.hour) }

    var hourAngle by remember { mutableDoubleStateOf(value = 0.0) }

    // Colors
    val clockFaceColor = Color(0xFF1A1A2E)
    val clockCircleColor = Color(0xFF00BCD4)
    val timeNumberColor = android.graphics.Color.parseColor("#E0E0E0")
    val secondLineColor = Color(0xFF607D8B)
    val eachFiveSecondLineColor = Color(0xFF90A4AE)

    val centerCircleColor = Color(0xFF00BCD4)
    val hourLineColor = Color(0xFFE0E0E0)
    val minuteLineColor = Color(0xFFBDBDBD)
    val longSecondLineColor = Color(0xFFFF5252)

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
        hourAngle = (minutes / 60.0 * 30.0) - 90.0 + (hours * 30)
    }

    LaunchedEffect(isClockRunning) {
        while (isClockRunning) {
            delay(1000L)
            seconds += 1

            if (seconds >= 60) {
                seconds = 0
                minutes++
            }
            if (minutes >= 60) {
                minutes = 0
                hours = (hours + 1) % 24
            }
        }
    }

    BoxWithConstraints {
        val width = if (minWidth < 1.dp) minSize else minWidth
        val height = if (minHeight < 1.dp) minSize else minHeight

        Canvas(
            modifier = modifier
                .size(width, height)
        ) {
            // calculate radius (60% of the width for responsiveness)
            val radius = size.width * .60f

            // Draw filled clock face background
            drawCircle(
                color = clockFaceColor,
                radius = radius,
                center = size.center
            )

            // Draw clock outline
            drawCircle(
                color = clockCircleColor,
                style = Stroke(width = radius * .03f),
                radius = radius,
                center = size.center
            )

            // Draw all tick marks
            val angleDegreeDifference = (360f / 60f)

            (1..60).forEach {
                val angleRadDifference =
                    (((angleDegreeDifference * it) - 90f) * (PI / 180f)).toFloat()

                val isHourMark = it % 5 == 0
                val lineLength = if (isHourMark) radius * .85f else radius * .92f
                val lineColour = if (isHourMark) eachFiveSecondLineColor else secondLineColor
                val strokeWidth = if (isHourMark) radius * .018f else radius * .008f

                val startOffsetLine = Offset(
                    x = lineLength * cos(angleRadDifference) + size.center.x,
                    y = lineLength * sin(angleRadDifference) + size.center.y
                )
                val endOffsetLine = Offset(
                    x = (radius - ((radius * .03f) / 2)) * cos(angleRadDifference) + size.center.x,
                    y = (radius - ((radius * .03f) / 2)) * sin(angleRadDifference) + size.center.y
                )
                drawLine(
                    color = lineColour,
                    start = startOffsetLine,
                    end = endOffsetLine,
                    strokeWidth = strokeWidth,
                )

                // Draw hour numbers (1-12)
                if (isHourMark) {
                    drawContext.canvas.nativeCanvas.apply {
                        val positionX =
                            (radius * .72f) * cos(angleRadDifference) + size.center.x
                        val positionY =
                            (radius * .72f) * sin(angleRadDifference) + size.center.y

                        val text = (it / 5).toString()
                        val paint = android.graphics.Paint().apply {
                            textSize = radius * .13f
                            color = timeNumberColor
                            isAntiAlias = true
                            typeface = android.graphics.Typeface.DEFAULT_BOLD
                        }

                        val textRect = android.graphics.Rect()
                        paint.getTextBounds(text, 0, text.length, textRect)

                        drawText(
                            text,
                            positionX - (textRect.width() / 2f),
                            positionY + (textRect.height() / 2f),
                            paint
                        )
                    }
                }
            }

            // Draw center dot
            drawCircle(
                color = centerCircleColor,
                radius = radius * .04f,
                center = size.center
            )

            // Hour hand
            drawLine(
                color = hourLineColor,
                start = size.center,
                end = Offset(
                    x = (radius * .55f) * cos((hourAngle * (PI / 180)).toFloat()) + size.center.x,
                    y = (radius * .55f) * sin((hourAngle * (PI / 180)).toFloat()) + size.center.y,
                ),
                strokeWidth = radius * .025f,
                cap = StrokeCap.Round
            )

            // Minute hand
            val minutesAngle = (seconds / 60.0 * 6.0) - 90.0 + (minutes * 6.0)
            drawLine(
                color = minuteLineColor,
                start = size.center,
                end = Offset(
                    x = (radius * .75f) * cos((minutesAngle * (PI / 180)).toFloat()) + size.center.x,
                    y = (radius * .75f) * sin((minutesAngle * (PI / 180)).toFloat()) + size.center.y
                ),
                strokeWidth = radius * .015f,
                cap = StrokeCap.Round
            )

            // Second hand
            drawLine(
                color = longSecondLineColor,
                start = Offset(
                    x = (radius * .15f) * cos(seconds.secondsToRad() + PI.toFloat()) + size.center.x,
                    y = (radius * .15f) * sin(seconds.secondsToRad() + PI.toFloat()) + size.center.y
                ),
                end = Offset(
                    x = (radius * .88f) * cos(seconds.secondsToRad()) + size.center.x,
                    y = (radius * .88f) * sin(seconds.secondsToRad()) + size.center.y
                ),
                strokeWidth = 2.5f.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Center dot (drawn last so it's on top)
            drawCircle(
                color = centerCircleColor,
                radius = radius * .025f,
                center = size.center
            )

            // Paused indicator
            if (!isClockRunning) {
                drawContext.canvas.nativeCanvas.apply {
                    val text = "PAUSED"
                    val paint = android.graphics.Paint().apply {
                        textSize = radius * .12f
                        color = android.graphics.Color.parseColor("#FF5252")
                        isAntiAlias = true
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                    }

                    val textRect = android.graphics.Rect()
                    paint.getTextBounds(text, 0, text.length, textRect)

                    drawText(
                        text,
                        size.center.x - (textRect.width() / 2f),
                        size.center.y + (radius * .4f),
                        paint
                    )
                }
            }
        }
    }
}

//return radians
fun Int.secondsToRad(): Float {
    val angle = (360f / 60f * this) - 90f
    return (angle * (PI / 180f)).toFloat()
}