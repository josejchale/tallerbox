package com.tallerbox.app.utils.pdf

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.tallerbox.app.model.orden.CondicionVehiculo
import com.tallerbox.app.model.orden.OrdenConClienteYVehiculo
import com.tallerbox.app.utils.pdf.PdfPaints

object VehicleConditionsBlock {

    fun draw(
        canvas: Canvas,
        data: OrdenConClienteYVehiculo,
        pageWidth: Int,
        startY: Float,
        margin: Float
    ): Float {

        val orden = data.orden
        val paintLine = PdfPaints.line
        val paintBody = PdfPaints.body
        val paintTableHeader = PdfPaints.tableHeader
        val paintBoxTitle = PdfPaints.boxTitle
        val paintX = PdfPaints.paintX

        var y = startY

        // === Título ===
        canvas.drawText("CONDICIONES DEL VEHÍCULO", margin, y, paintBoxTitle)
        y += 6f

        val condiciones = orden.condiciones

        // === Columnas ===
        val itemsLeft = listOf(
            "Espejos", "Asientos", "Faro delantero", "Luz de trasera",
            "Direccionales", "Cubiertas", "Tapón de gasolina"
        )
        val itemsRight = listOf(
            "Pedales", "Parabrisas", "Claxon", "Tapón de aceite",
            "Tapón radiador", "Filtro de aire", "Batería", "Llaves"
        )

        val condStartY = y
        val condRowHeight = 16f
        val condColGap = 12f
        val condColWidth = (pageWidth - margin * 2 - condColGap) / 2f

        val labels = listOf("PRESENTA", "SI", "NO", "ROTO/DAÑADO")
        val labelWidths = floatArrayOf(
            condColWidth * 0.45f,
            condColWidth * 0.16f,
            condColWidth * 0.16f,
            condColWidth * 0.23f
        )

        val condTextYOffset = 12f
        val condHeaderYOffset = 12f

        val condHeaderPaint = Paint().apply {
            style = Paint.Style.FILL
            color = Color.LTGRAY
        }

        // === Encabezados columna izquierda ===
        var headerX = margin
        val headerY = condStartY

        for (i in labels.indices) {
            val w = labelWidths[i]
            canvas.drawRect(headerX, headerY, headerX + w, headerY + condRowHeight, condHeaderPaint)
            canvas.drawRect(headerX, headerY, headerX + w, headerY + condRowHeight, paintLine)

            val text = labels[i]
            val tx = headerX + (w / 2f) - (paintTableHeader.measureText(text) / 2f)
            val ty = headerY + condHeaderYOffset
            canvas.drawText(text, tx, ty, paintTableHeader)

            headerX += w
        }

        // === Encabezados columna derecha ===
        headerX = margin + condColWidth + condColGap
        for (i in labels.indices) {
            val w = labelWidths[i]
            canvas.drawRect(headerX, headerY, headerX + w, headerY + condRowHeight, condHeaderPaint)
            canvas.drawRect(headerX, headerY, headerX + w, headerY + condRowHeight, paintLine)

            val text = labels[i]
            val tx = headerX + (w / 2f) - (paintTableHeader.measureText(text) / 2f)
            val ty = headerY + condHeaderYOffset
            canvas.drawText(text, tx, ty, paintTableHeader)

            headerX += w
        }

        // === Obtener estado ===
        fun getCondValueByName(cond: CondicionVehiculo?, name: String): String {
            val enumValue = when (name) {
                "Espejos" -> cond?.espejos
                "Asientos" -> cond?.asientos
                "Faro delantero" -> cond?.faroDelantero
                "Luz de trasera" -> cond?.luzTrasera
                "Direccionales" -> cond?.direccionales
                "Cubiertas" -> cond?.cubiertas
                "Tapón de gasolina" -> cond?.taponGasolina
                "Pedales" -> cond?.pedales
                "Parabrisas" -> cond?.parabrisas
                "Claxon" -> cond?.claxon
                "Tapón de aceite" -> cond?.taponAceite
                "Tapón radiador" -> cond?.taponRadiador
                "Filtro de aire" -> cond?.filtroAire
                "Batería" -> cond?.bateria
                "Llaves" -> cond?.llaves
                else -> null
            }
            return enumValue?.name ?: ""
        }

        // === Dibujar filas columna izquierda ===
        var rowYLeft = condStartY + condRowHeight
        for (item in itemsLeft) {
            var colX = margin

            val condValue = getCondValueByName(condiciones, item)
            val markCol = when (condValue) {
                "SI" -> 1
                "NO" -> 2
                "ROTO_DANADO" -> 3
                else -> -1
            }

            for (i in 0 until 4) {
                val w = labelWidths[i]
                canvas.drawRect(colX, rowYLeft, colX + w, rowYLeft + condRowHeight, paintLine)
                if (i == 0) canvas.drawText(item, colX + 4f, rowYLeft + condTextYOffset, paintBody)
                colX += w
            }

            if (markCol >= 1) {
                val xMark = margin + labelWidths.take(markCol).sum() + (labelWidths[markCol] / 2f) - (paintX.measureText("X") / 2f)
                val yMark = rowYLeft + condRowHeight - 4f
                canvas.drawText("X", xMark, yMark, paintX)
            }

            rowYLeft += condRowHeight
        }

        // === Dibujar filas columna derecha ===
        var rowYRight = condStartY + condRowHeight
        for (item in itemsRight) {
            var colX = margin + condColWidth + condColGap

            val condValue = getCondValueByName(condiciones, item)
            val markCol = when (condValue) {
                "SI" -> 1
                "NO" -> 2
                "ROTO_DANADO" -> 3
                else -> -1
            }

            for (i in 0 until 4) {
                val w = labelWidths[i]
                canvas.drawRect(colX, rowYRight, colX + w, rowYRight + condRowHeight, paintLine)
                if (i == 0) canvas.drawText(item, colX + 4f, rowYRight + condTextYOffset, paintBody)
                colX += w
            }

            if (markCol >= 1) {
                val xMark = margin + condColWidth + condColGap + labelWidths.take(markCol).sum() + (labelWidths[markCol] / 2f) - (paintX.measureText("X") / 2f)
                val yMark = rowYRight + condRowHeight - 4f
                canvas.drawText("X", xMark, yMark, paintX)
            }

            rowYRight += condRowHeight
        }

        return maxOf(rowYLeft, rowYRight) + 10f
    }
}
