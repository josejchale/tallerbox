package com.tallerbox.app.utils.pdf.tables

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.tallerbox.app.model.orden.OrdenConClienteYVehiculo
import com.tallerbox.app.utils.pdf.TextHelpers.drawMultilineText
import com.tallerbox.app.utils.pdf.PdfPaints

object ClientOrderAndServiceDataBlock {

    fun draw(
        canvas: Canvas,
        data: OrdenConClienteYVehiculo,
        pageWidth: Int,
        startY: Float,
        margin: Float
    ): Float {

        val paintSectionTitle = PdfPaints.sectionTitle
        val paintLine = PdfPaints.line
        val paintBoxTitle = PdfPaints.boxTitle
        val paintBody = PdfPaints.body

        var y = startY

        // === TÍTULO CENTRADO ===
        val title = "ORDEN DE SERVICIO"
        val titleX = (pageWidth / 2f) - (paintSectionTitle.measureText(title) / 2f)
        canvas.drawText(title, titleX, y, paintSectionTitle)
        y += 16f

        // === CONFIG BLOQUES ===
        val blockSpacing = 12f
        val blockWidth = ((pageWidth - margin * 2) - blockSpacing) / 2f
        val leftColX = margin
        val leftColRight = leftColX + blockWidth
        val rightColX = leftColRight + blockSpacing
        val rightColRight = rightColX + blockWidth

        val blockTop = y
        val titleRowHeight = 16f

        val clientRow1Height = 16f
        val clientRow2Height = 32f
        val clientRow3Height = 16f
        val clientBlockHeight = titleRowHeight + clientRow1Height + clientRow2Height + clientRow3Height

        val orderBlockHeight = clientBlockHeight
        val orderRowHeight = (orderBlockHeight - titleRowHeight) / 3f

        // === FONDO GRIS PARA ENCABEZADOS ===
        val headerFillPaint = Paint().apply {
            style = Paint.Style.FILL
            color = Color.LTGRAY
        }

        // Encabezados
        canvas.drawRect(leftColX, blockTop, leftColRight, blockTop + titleRowHeight, headerFillPaint)
        canvas.drawRect(rightColX, blockTop, rightColRight, blockTop + titleRowHeight, headerFillPaint)

        // Bordes
        canvas.drawRect(leftColX, blockTop, leftColRight, blockTop + titleRowHeight, paintLine)
        canvas.drawRect(rightColX, blockTop, rightColRight, blockTop + titleRowHeight, paintLine)
        canvas.drawRect(leftColX, blockTop, leftColRight, blockTop + clientBlockHeight, paintLine)
        canvas.drawRect(rightColX, blockTop, rightColRight, blockTop + orderBlockHeight, paintLine)

        // === TÍTULOS DE LOS BLOQUES ===
        val titleYOffset = 12f

        val leftTitleX = leftColX + (blockWidth / 2f) - (paintBoxTitle.measureText("DATOS DEL CLIENTE") / 2f)
        canvas.drawText("DATOS DEL CLIENTE", leftTitleX, blockTop + titleYOffset, paintBoxTitle)

        val rightTitleX = rightColX + (blockWidth / 2f) - (paintBoxTitle.measureText("DATOS DE ORDEN DE SERVICIO") / 2f)
        canvas.drawText("DATOS DE ORDEN DE SERVICIO", rightTitleX, blockTop + titleYOffset, paintBoxTitle)

        // === SEPARADORES HORIZONTALES ===
        val leftRow1 = blockTop + titleRowHeight
        val leftRow2 = leftRow1 + clientRow1Height
        val leftRow3 = leftRow2 + clientRow2Height

        canvas.drawLine(leftColX, leftRow2, leftColRight, leftRow2, paintLine)
        canvas.drawLine(leftColX, leftRow3, leftColRight, leftRow3, paintLine)

        val rightRow1 = blockTop + titleRowHeight
        val rightRow2 = rightRow1 + orderRowHeight
        val rightRow3 = rightRow2 + orderRowHeight

        canvas.drawLine(rightColX, rightRow2, rightColRight, rightRow2, paintLine)
        canvas.drawLine(rightColX, rightRow3, rightColRight, rightRow3, paintLine)

        // === LÍNEAS VERTICALES INTERNAS ===
        val rightLabelWidth = 130f
        canvas.drawLine(rightColX + rightLabelWidth, blockTop + titleRowHeight, rightColX + rightLabelWidth, blockTop + orderBlockHeight, paintLine)

        val clientLabelWidth = 130f
        canvas.drawLine(leftColX + clientLabelWidth, blockTop + titleRowHeight, leftColX + clientLabelWidth, blockTop + clientBlockHeight, paintLine)

        // === LLENADO DE DATOS ===
        val cliente = data.cliente
        val orden = data.orden

        val nombre = cliente?.nombreCompleto ?: "—"
        val direccion = "${cliente?.calle ?: ""} ${cliente?.numeroCasa ?: ""}, entre calles ${cliente?.calle1} y ${cliente?.calle2}, ${cliente?.municipio ?: ""}, ${cliente?.estado ?: ""}".trim().ifBlank { "—" }
        val celular = cliente?.telefono ?: "—"

        val labelOffset = 6f
        val verticalOffsetStd = 12f
        val verticalOffsetDir = 12f

        val clientValueX = leftColX + clientLabelWidth + labelOffset
        val clientValueMaxWidth = blockWidth - clientLabelWidth - 12f

        // === CLIENTE ===
        var ly = leftRow1 + verticalOffsetStd
        canvas.drawText("NOMBRE:", leftColX + labelOffset, ly, paintBoxTitle)
        drawMultilineText(canvas, nombre, clientValueX, leftRow1 + 10f, clientValueMaxWidth, paintBody)

        ly = leftRow2 + verticalOffsetDir
        canvas.drawText("DIRECCIÓN:", leftColX + labelOffset, ly, paintBoxTitle)
        drawMultilineText(canvas, direccion, clientValueX, leftRow2 + 10f, clientValueMaxWidth, paintBody)

        ly = leftRow3 + verticalOffsetStd
        canvas.drawText("NÚMERO CELULAR:", leftColX + labelOffset, ly, paintBoxTitle)
        drawMultilineText(canvas, celular, clientValueX, leftRow3 + 10f, clientValueMaxWidth, paintBody)

        // === ORDEN ===
        val numeroOrden = orden.numeroOrden
        val fechaIngreso = orden.fechaIngreso?.toString() ?: "—"
        val fechaEntrega = orden.fechaEntregaEstimado?.toString() ?: "—"

        val rightLabelX = rightColX + 6f
        val rightValueX = rightColX + rightLabelWidth + 6f
        val rightValueMaxWidth = blockWidth - rightLabelWidth - 12f

        val rightVerticalOffset = (orderRowHeight / 2f) + 4f
        val rightTextYOffset = rightVerticalOffset - 4f

        var ry = rightRow1 + rightVerticalOffset
        canvas.drawText("NÚMERO DE ORDEN:", rightLabelX, ry, paintBoxTitle)
        drawMultilineText(canvas, numeroOrden, rightValueX, rightRow1 + rightTextYOffset, rightValueMaxWidth, paintBody)

        ry = rightRow2 + rightVerticalOffset
        canvas.drawText("FECHA DE INGRESO:", rightLabelX, ry, paintBoxTitle)
        drawMultilineText(canvas, fechaIngreso, rightValueX, rightRow2 + rightTextYOffset, rightValueMaxWidth, paintBody)

        ry = rightRow3 + rightVerticalOffset
        canvas.drawText("FECHA DE ENTREGA:", rightLabelX, ry, paintBoxTitle)
        drawMultilineText(canvas, fechaEntrega, rightValueX, rightRow3 + rightTextYOffset, rightValueMaxWidth, paintBody)

        return blockTop + clientBlockHeight + 20f
    }
}
