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
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Date
import java.util.Locale
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
    var seconds by remember { mutableStateOf(time.second + 4) }
    var minutes by remember { mutableStateOf(time.minute) }
    var hours by remember { mutableStateOf(time.hour + 1) }

    var hourAngle by remember { mutableDoubleStateOf(value = 0.0) }

    // Colors
    val clockCircleColor = Color.White
    val timeNumberColor = android.graphics.Color.WHITE
    val secondLineColor = Color.White
    val eachFiveSecondLineColor = Color.White

    val clockIngraviour = android.graphics.Color.RED
    val clockPausedText = android.graphics.Color.MAGENTA

    val centerCircleColor = Color.White
    val hourLineColor = Color.White
    val minuteLineColor = Color.White
    val longSecondLineColor = Color.Red

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
            seconds += 1

            if (seconds > 60) {
                seconds = 1
                minutes++
            }
            if (minutes > 60) {
                minutes = 1
                hours++
            }

            delay(1000L)
        }
    }

    BoxWithConstraints {
        val width = if (minWidth < 1.dp) minSize else minWidth
        val height = if (minHeight < 1.dp) minSize else minHeight

        Canvas(
            modifier = modifier
                .size(width, height)
        ) {
            //1. First Step
            // draw analog clock circle

            // calculate radius (40% of the radius for responsiveness)
            val radius = size.width * .60f
            drawCircle(
                color = clockCircleColor,
                style = Stroke(width = radius * .05f /* 5% of the radius*/),
                radius = radius,
                center = size.center
            )

            //2. Second Step
            // draw all second lines

            //The degree difference between the each 'minute' line
            val angleDegreeDifference = (360f / 60f)

            //drawing all 60 second lines
            (1..60).forEach {
                val angleRadDifference =
                    (((angleDegreeDifference * it) - 90f) * (PI / 180f)).toFloat()

                val lineLength = if (it % 5 == 0) radius * .88f else radius * .92f
                val lineColour = if (it % 5 == 0) eachFiveSecondLineColor else secondLineColor

                val startOffsetLine = Offset(
                    x = lineLength * cos(angleRadDifference) + size.center.x,
                    y = lineLength * sin(angleRadDifference) + size.center.y
                )
                val endOffsetLine = Offset(
                    x = (radius - ((radius * .05f) / 2)) * cos(angleRadDifference) + size.center.x,
                    y = (radius - ((radius * .05f) / 2)) * sin(angleRadDifference) + size.center.y
                )
                //draw second line
                drawLine(
                    color = lineColour,
                    start = startOffsetLine,
                    end = endOffsetLine,
                    strokeWidth = radius * .01f,
                )

                //draw second digit every 5th second
                if (it % 5 == 0) {
                    drawContext.canvas.nativeCanvas.apply {
                        val positionX =
                            (radius * .76f) * cos(angleRadDifference) + size.center.x
                        val positionY =
                            (radius * .76f) * sin(angleRadDifference) + size.center.y

                        val text = (it / 5).toString()
                        val paint = android.graphics.Paint()
                        paint.textSize = radius * .15f
                        paint.color = timeNumberColor

                        val textRect = android.graphics.Rect()
                        paint.getTextBounds(text, 0, text.length, textRect)

                        drawText(
                            text,
                            positionX - (textRect.width() / 2),
                            positionY + (textRect.width() / 2),
                            paint
                        )
                    }
                }
            }

            //Audi Sports
            drawContext.canvas.nativeCanvas.apply {
                val text = "Audi Sports"
                val paint = android.graphics.Paint()
                paint.textSize = radius * .15f
                paint.color = clockIngraviour

                val textRect = android.graphics.Rect()
                paint.getTextBounds(text, 0, text.length, textRect)

                drawText(
                    text,
                    size.center.x - (textRect.width() / 2),
                    size.center.y - 200,
                    paint
                )
            }

            //now draw the center of the screen
            drawCircle(
                color = centerCircleColor,
                radius = radius * .03f, //only 2% of the main radius
                center = size.center
            )

            //hour-line hand
            drawLine(
                color = hourLineColor,
                start = size.center,
                end = Offset(
                    x = (radius * .55f) * cos((hourAngle * (PI / 180)).toFloat()) + size.center.x,
                    y = (radius * .55f) * sin((hourAngle * (PI / 180)).toFloat()) + size.center.y,
                ),
                strokeWidth = radius * .02f,
                cap = StrokeCap.Square
            )

            //minutes hand - just dividing the seconds with 60 and multiplying it by 6 degrees (which is the difference between the lines)
            // subtracting 90 as the 0degrees is actually at 3 o'clock
            val minutesAngle = (seconds / 60.0 * 6.0) - 90.0 + (minutes * 6.0)
            drawLine(
                color = minuteLineColor,
                start = size.center,
                end = Offset(
                    x = (radius * .7f) * cos((minutesAngle * (PI / 180)).toFloat()) + size.center.x,
                    y = (radius * .7f) * sin((minutesAngle * (PI / 180)).toFloat()) + size.center.y
                ),
                strokeWidth = radius * .01f,
                cap = StrokeCap.Square
            )

            //seconds line hand
            drawLine(
                color = longSecondLineColor,
                start = size.center,
                end = Offset(
                    x = (radius * .9f) * cos(seconds.secondsToRad()) + size.center.x,
                    y = (radius * .9f) * sin(seconds.secondsToRad()) + size.center.y
                ),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )

            //paused text
            if (!isClockRunning) {
                drawContext.canvas.nativeCanvas.apply {
                    val text = "PAUSED"
                    val paint = android.graphics.Paint()
                    paint.textSize = radius * .15f
                    paint.color = clockPausedText

                    val textRect = android.graphics.Rect()
                    paint.getTextBounds(text, 0, text.length, textRect)

                    drawText(
                        text,
                        size.center.x - (textRect.width() / 2),
                        size.center.y + (textRect.width() / 2),
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