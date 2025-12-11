package com.tallerbox.app.utils.pdf

import android.content.Context
import android.graphics.*
import com.tallerbox.app.R
import com.tallerbox.app.utils.pdf.PdfPaints
import com.tallerbox.app.utils.pdf.TextHelpers

object PdfHeaderDrawer {

    fun drawHeader(context: Context, canvas: Canvas, pageWidth: Int, margin: Float): Float {
        val paintHeaderTitle = PdfPaints.headerTitle
        val paintHeaderInfo = PdfPaints.headerInfo
        val paintLine = PdfPaints.line

        var y = margin
        try {
            val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.moto)
            val headerTop = margin
            val headerLeft = margin
            val headerRight = pageWidth - margin

            val desiredLogoWidth = 100f
            val logoPadding = 5f
            val aspectRatio = logoBitmap.height.toFloat() / logoBitmap.width
            val finalLogoWidth = desiredLogoWidth
            val finalLogoHeight = finalLogoWidth * aspectRatio
            val logoAreaWidth = finalLogoWidth + logoPadding * 2
            val textStartX = headerLeft + logoAreaWidth + 10f
            val textMaxWidthForMeasurement = headerRight - textStartX - 5f

            val direccionTaller = "DIRECCIÓN: CALLE 21 #30K ENTRE 10 Y 12 COL. SAN FRANCISCO"
            val spacingBetweenTitleAndInfo = 6f
            val spacingBetweenInfoLines = 8f

            val fmTitle = paintHeaderTitle.fontMetrics
            val fmInfo = paintHeaderInfo.fontMetrics

            var tempY = 0f
            tempY += (fmTitle.bottom - fmTitle.top) * 2 + spacingBetweenTitleAndInfo
            tempY += (fmInfo.bottom - fmInfo.top) + spacingBetweenInfoLines

            val textPaint = android.text.TextPaint(paintHeaderInfo)
            val staticLayout = android.text.StaticLayout.Builder.obtain(
                direccionTaller, 0, direccionTaller.length, textPaint,
                textMaxWidthForMeasurement.toInt()
            ).setAlignment(android.text.Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1f)
                .setIncludePad(false)
                .build()

            val textHeightDirection = staticLayout.height.toFloat()
            tempY += textHeightDirection

            val netTextContentHeight = tempY
            val textBlockPadding = 8f
            val totalTextBlockHeight = netTextContentHeight + textBlockPadding

            val minLogoHeight = 80f
            val calculatedLogoHeight = maxOf(minLogoHeight, finalLogoHeight)
            val contentHeight = maxOf(calculatedLogoHeight + logoPadding * 2, totalTextBlockHeight)
            val headerBottom = headerTop + contentHeight

            val adjustedLogoHeight = minOf(finalLogoHeight, contentHeight - logoPadding * 2)
            val adjustedLogoWidth = adjustedLogoHeight / aspectRatio

            val textBlockLeft = textStartX - 8f
            val cornerCut = 20f
            val path = Path().apply {
                moveTo(textBlockLeft, headerTop)
                lineTo(headerRight - cornerCut, headerTop)
                lineTo(headerRight, headerTop + cornerCut)
                lineTo(headerRight, headerBottom)
                lineTo(textBlockLeft, headerBottom)
                close()
            }
            canvas.drawPath(path, paintLine)

            val logoX = headerLeft + logoPadding
            val logoY = headerTop + (contentHeight - adjustedLogoHeight) / 2f
            val logoDestRect = RectF(
                logoX,
                logoY,
                logoX + adjustedLogoWidth,
                logoY + adjustedLogoHeight
            )
            canvas.drawBitmap(logoBitmap, null, logoDestRect, null)

            var currentTextY = headerTop + (contentHeight - netTextContentHeight) / 2f

            canvas.drawText("TALLER DE MOTOS", textStartX, currentTextY + fmTitle.ascent.times(-1), paintHeaderTitle)
            currentTextY += fmTitle.bottom - fmTitle.top

            canvas.drawText("~BOX HALACHÓ~", textStartX, currentTextY + fmTitle.ascent.times(-1), paintHeaderTitle)
            currentTextY += (fmTitle.bottom - fmTitle.top) + spacingBetweenTitleAndInfo

            canvas.drawText("TELÉFONO: 999-333-68-77", textStartX, currentTextY + fmInfo.ascent.times(-1), paintHeaderInfo)
            currentTextY += (fmInfo.bottom - fmInfo.top) + spacingBetweenInfoLines

            val finalYAfterDirection = TextHelpers.drawMultilineText(canvas, direccionTaller, textStartX, currentTextY, textMaxWidthForMeasurement, paintHeaderInfo)

            y = maxOf(finalYAfterDirection, headerBottom) + 12f

        } catch (e: Exception) {
            android.util.Log.w("PdfHeaderDrawer", "Error drawing header", e)
            y = margin + 100f + 12f
        }

        return y
    }
}

