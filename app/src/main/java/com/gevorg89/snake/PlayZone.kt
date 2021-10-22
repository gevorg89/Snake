package com.gevorg89.snake

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp

@Composable
fun DrawPlayZone(
    width: Dp,
    height: Dp,
    blockWidth: Int,
    blockHeight: Int,
    widthPx: Float,
    heightPx: Float,
    blockSizeHeight: Float,
    blockSizeWidth: Float,
    onDraw: DrawScope.() -> Unit
) {
    Canvas(
        modifier = Modifier
            .background(Color.Gray)
            .width(width)
            .height(height)
    ) {
        for (i in 1..blockHeight + 1) {
            drawLine(
                color = Color.Blue,
                start = Offset(0f, i * blockSizeHeight),
                end = Offset(widthPx, i * blockSizeHeight)
            )
        }
        for (i in 1..blockWidth + 1) {
            drawLine(
                color = Color.Green,
                start = Offset(i * blockSizeWidth, 0f),
                end = Offset(i * blockSizeWidth, heightPx)
            )
        }
        onDraw()
    }
}