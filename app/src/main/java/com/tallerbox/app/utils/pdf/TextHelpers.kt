package com.tallerbox.app.utils.pdf

import android.graphics.Canvas
import android.graphics.Paint

object TextHelpers {


    fun drawMultilineText(
        canvas: Canvas,
        text: String,
        startX: Float,
        startY: Float,
        maxWidth: Float,
        paint: Paint
    ): Float {
        var currentY = startY
        text.split('\n').forEach { line ->
            val words = line.split(Regex("\\s+"))
            var buffer = ""
            for (w in words) {
                val test = if (buffer.isEmpty()) w else "$buffer $w"
                if (paint.measureText(test) > maxWidth && buffer.isNotEmpty()) {
                    canvas.drawText(buffer, startX, currentY, paint)
                    currentY += paint.fontSpacing
                    buffer = w
                } else {
                    buffer = test
                }
            }
            if (buffer.isNotEmpty()) {
                canvas.drawText(buffer, startX, currentY, paint)
                currentY += paint.fontSpacing
            }
        }
        return currentY
    }


    fun drawRichMultilineText(
        canvas: Canvas,
        text: String,
        startX: Float,
        startY: Float,
        maxWidth: Float,
        normalPaint: Paint,
        boldPaint: Paint
    ): Float {
        var y = startY
        val lines = text.split("\n")


        for (line in lines) {
            if (line.isBlank()) {
                y += normalPaint.textSize * 1.5f
                continue
            }


            val parts = Regex("(\\*\\*.*?\\*\\*)|([^*]+)").findAll(line)
            var x = startX


            for (p in parts) {
                val segment = p.value
                val isBold = segment.startsWith("**") && segment.endsWith("**")
                val cleanText = if (isBold) segment.substring(2, segment.length - 2) else segment
                val paint = if (isBold) boldPaint else normalPaint
                val words = cleanText.split(" ")
                for (word in words) {
                    val wordText = "$word "
                    val wordWidth = paint.measureText(wordText)
                    if (x + wordWidth > startX + maxWidth) {
                        x = startX
                        y += normalPaint.textSize * 1.5f
                    }
                    canvas.drawText(wordText, x, y, paint)
                    x += wordWidth
                }
            }
            y += normalPaint.textSize * 1.5f
        }


        return y
    }
}