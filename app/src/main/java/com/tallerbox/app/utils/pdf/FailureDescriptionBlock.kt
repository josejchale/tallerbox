package com.tallerbox.app.utils.pdf

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.tallerbox.app.model.orden.OrdenConClienteYVehiculo
import com.tallerbox.app.utils.pdf.TextHelpers.drawMultilineText
import com.tallerbox.app.utils.pdf.PdfPaints

object FailureDescriptionBlock {

    fun draw(
        canvas: Canvas,
        data: OrdenConClienteYVehiculo,
        pageWidth: Int,
        startY: Float,
        margin: Float
    ): Float {

        val paintLine = PdfPaints.line
        val paintBoxTitle = PdfPaints.boxTitle
        val paintBody = PdfPaints.body

        var y = startY

        // === Encabezado sombreado gris ===
        val headerFillPaint = Paint().apply {
            style = Paint.Style.FILL
            color = Color.LTGRAY
        }

        val descHeaderHeight = 16f
        canvas.drawRect(margin, y, pageWidth - margin, y + descHeaderHeight, headerFillPaint)
        canvas.drawRect(margin, y, pageWidth - margin, y + descHeaderHeight, paintLine)

        val descTitleX = margin + ((pageWidth - margin * 2) / 2f) - (paintBoxTitle.measureText("DESCRIPCIÓN DE FALLA") / 2f)
        val textYOffset = 12f
        canvas.drawText("DESCRIPCIÓN DE FALLA", descTitleX, y + textYOffset, paintBoxTitle)

        y += descHeaderHeight

        // === Cuadro de descripción ===
        val descTop = y
        val descHeight = 32f

        canvas.drawRect(margin, descTop, pageWidth - margin, descTop + descHeight, paintLine)

        val orden = data.orden
        val falla = orden.descripcionFalla ?: "—"

        val textX = margin + 6f
        val maxWidth = pageWidth - margin * 2 - 12f

        drawMultilineText(canvas, falla, textX, descTop + 10f, maxWidth, paintBody)

        return descTop + descHeight + 20f
    }
}
