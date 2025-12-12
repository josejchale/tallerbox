package com.tallerbox.app.utils.pdf

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.tallerbox.app.model.orden.OrdenConClienteYVehiculo
import com.tallerbox.app.utils.pdf.TextHelpers.drawMultilineText
import com.tallerbox.app.utils.pdf.PdfPaints

object VehicleDataTable {

    fun draw(
        canvas: Canvas,
        data: OrdenConClienteYVehiculo,
        pageWidth: Int,
        startY: Float,
        margin: Float
    ): Float {

        val paintLine = PdfPaints.line
        val paintBoxTitle = PdfPaints.boxTitle
        val paintTableHeader = PdfPaints.tableHeader
        val paintBody = PdfPaints.body

        var y = startY

        // === Datos del vehículo ===
        val veh = data.vehiculo
        val columns = listOf("MARCA", "MODELO", "AÑO", "COLOR", "PLACAS", "VIN")
        val vehValues = listOf(
            veh?.marca ?: "—",
            veh?.modelo ?: "—",
            veh?.ano?.toString() ?: "—",
            veh?.color ?: "—",
            veh?.placa ?: "—",
            veh?.vin ?: "—"
        )

        val vehTableLeft = margin
        val vehTableRight = pageWidth - margin
        val vehTableWidth = vehTableRight - vehTableLeft

        val colWidths = floatArrayOf(
            vehTableWidth * 0.15f, // MARCA
            vehTableWidth * 0.15f, // MODELO
            vehTableWidth * 0.10f, // AÑO
            vehTableWidth * 0.15f, // COLOR
            vehTableWidth * 0.12f, // PLACAS
            vehTableWidth * 0.33f  // VIN
        )

        val headerRowHeight = 16f
        val valueRowHeight = 16f
        val textYOffset = 12f

        // === ENCABEZADO "DATOS DEL VEHÍCULO" ===
        val headerFillPaint = Paint().apply {
            style = Paint.Style.FILL
            color = Color.LTGRAY
        }

        canvas.drawRect(vehTableLeft, y, vehTableRight, y + headerRowHeight, headerFillPaint)
        canvas.drawRect(vehTableLeft, y, vehTableRight, y + headerRowHeight, paintLine)

        val titleTextX = vehTableLeft + (vehTableWidth / 2f) - (paintBoxTitle.measureText("DATOS DEL VEHÍCULO") / 2f)
        canvas.drawText("DATOS DEL VEHÍCULO", titleTextX, y + textYOffset, paintBoxTitle)

        y += headerRowHeight

        // === ENCABEZADOS DE TABLA ===
        val headerTop = y
        val headerBottom = headerTop + headerRowHeight
        var vx = vehTableLeft

        for (i in columns.indices) {
            val c = columns[i]
            val w = colWidths[i]
            val cellLeft = vx
            val cellRight = vx + w

            canvas.drawRect(cellLeft, headerTop, cellRight, headerBottom, paintLine)

            val textWidth = paintTableHeader.measureText(c)
            val tx = cellLeft + (w / 2f) - (textWidth / 2f)
            val ty = headerTop + textYOffset

            canvas.drawText(c, tx, ty, paintTableHeader)
            vx += w
        }

        // === VALORES ===
        val valuesTop = headerBottom
        val valuesBottom = valuesTop + valueRowHeight
        vx = vehTableLeft

        for (i in vehValues.indices) {
            val v = vehValues[i]
            val w = colWidths[i]

            canvas.drawRect(vx, valuesTop, vx + w, valuesBottom, paintLine)

            val valueMaxWidth = w - 8f
            drawMultilineText(canvas, v, vx + 4f, valuesTop + textYOffset - 2f, valueMaxWidth, paintBody)

            vx += w
        }

        return valuesBottom + 12f
    }
}
