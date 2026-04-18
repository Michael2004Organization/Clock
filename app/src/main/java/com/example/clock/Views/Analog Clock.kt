package com.example.clock.Views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.clock.ui.theme.IceBlue
import com.example.clock.ui.theme.SoftWhite
import com.example.clock.ui.theme.SunsetOrange
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

    val dialColor = SoftWhite.copy(alpha = 0.95f)
    val majorTickColor = SoftWhite
    val minorTickColor = SoftWhite.copy(alpha = 0.65f)
    val numberColor = android.graphics.Color.WHITE
    val centerDotColor = IceBlue
    val hourHandColor = SoftWhite
    val minuteHandColor = IceBlue
    val secondHandColor = SunsetOrange
    val pausedColor = android.graphics.Color.rgb(255, 182, 72)

    LaunchedEffect(isClockRunning) {
        if (isClockRunning) {
            val currentTime = withContext(Dispatchers.IO) { ClockData.getAtomTime() }
            hours = currentTime.hour.toIntOrNull() ?: time.hour
            minutes = currentTime.minute.toIntOrNull() ?: time.minute
            seconds = currentTime.second.toIntOrNull() ?: time.second
        }
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
                hours = (hours + 1) % 24
            }
        }
    }

    BoxWithConstraints {
        val width = if (minWidth < 1.dp) minSize else minWidth
        val height = if (minHeight < 1.dp) minSize else minHeight

        Canvas(
            modifier = modifier.size(width, height)
        ) {
            val radius = size.minDimension * 0.46f

            drawCircle(
                color = dialColor,
                style = Stroke(width = radius * 0.08f),
                radius = radius,
                center = size.center
            )

            (0 until 60).forEach { tick ->
                val angleInRad = ((tick * 6f - 90f) * (PI / 180f)).toFloat()
                val isMajorTick = tick % 5 == 0

                val startRadius = if (isMajorTick) radius * 0.78f else radius * 0.86f
                val start = Offset(
                    x = startRadius * cos(angleInRad) + size.center.x,
                    y = startRadius * sin(angleInRad) + size.center.y
                )
                val end = Offset(
                    x = radius * cos(angleInRad) + size.center.x,
                    y = radius * sin(angleInRad) + size.center.y
                )

                drawLine(
                    color = if (isMajorTick) majorTickColor else minorTickColor,
                    start = start,
                    end = end,
                    strokeWidth = if (isMajorTick) radius * 0.018f else radius * 0.008f,
                    cap = StrokeCap.Round
                )

                if (isMajorTick) {
                    drawContext.canvas.nativeCanvas.apply {
                        val number = if (tick == 0) "12" else (tick / 5).toString()
                        val paint = android.graphics.Paint()
                        paint.textSize = radius * 0.18f
                        paint.color = numberColor
                        paint.isAntiAlias = true

                        val textBounds = android.graphics.Rect()
                        paint.getTextBounds(number, 0, number.length, textBounds)

                        val textRadius = radius * 0.65f
                        val textX = textRadius * cos(angleInRad) + size.center.x
                        val textY = textRadius * sin(angleInRad) + size.center.y

                        drawText(
                            number,
                            textX - (textBounds.width() / 2),
                            textY + (textBounds.height() / 2),
                            paint
                        )
                    }
                }
            }

            val hourAngle = (((hours % 12) + (minutes / 60f)) * 30f) - 90f
            val minuteAngle = ((minutes + (seconds / 60f)) * 6f) - 90f

            drawLine(
                color = hourHandColor,
                start = size.center,
                end = Offset(
                    x = (radius * 0.52f) * cos((hourAngle * (PI / 180)).toFloat()) + size.center.x,
                    y = (radius * 0.52f) * sin((hourAngle * (PI / 180)).toFloat()) + size.center.y,
                ),
                strokeWidth = radius * 0.03f,
                cap = StrokeCap.Round
            )

            drawLine(
                color = minuteHandColor,
                start = size.center,
                end = Offset(
                    x = (radius * 0.72f) * cos((minuteAngle * (PI / 180)).toFloat()) + size.center.x,
                    y = (radius * 0.72f) * sin((minuteAngle * (PI / 180)).toFloat()) + size.center.y,
                ),
                strokeWidth = radius * 0.018f,
                cap = StrokeCap.Round
            )

            drawLine(
                color = secondHandColor,
                start = size.center,
                end = Offset(
                    x = (radius * 0.85f) * cos(seconds.secondsToRadians()) + size.center.x,
                    y = (radius * 0.85f) * sin(seconds.secondsToRadians()) + size.center.y,
                ),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )

            drawCircle(
                color = centerDotColor,
                radius = radius * 0.04f,
                center = size.center
            )

            if (!isClockRunning) {
                drawContext.canvas.nativeCanvas.apply {
                    val text = "PAUSED"
                    val paint = android.graphics.Paint()
                    paint.textSize = radius * 0.16f
                    paint.color = pausedColor
                    paint.isFakeBoldText = true

                    val textRect = android.graphics.Rect()
                    paint.getTextBounds(text, 0, text.length, textRect)

                    drawText(
                        text,
                        size.center.x - (textRect.width() / 2),
                        size.center.y + (textRect.height() / 2),
                        paint
                    )
                }
            }
        }
    }
}

private fun Int.secondsToRadians(): Float {
    val angle = (360f / 60f * this) - 90f
    return (angle * (PI / 180f)).toFloat()
}
