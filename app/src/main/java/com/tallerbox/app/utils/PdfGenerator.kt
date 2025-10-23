package com.tallerbox.app.utils

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Base64
import androidx.core.content.FileProvider
import com.tallerbox.app.model.orden.OrdenConClienteYVehiculo
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfGenerator {

    // Carta: 612 x 792 puntos
    private const val PAGE_WIDTH = 612
    private const val PAGE_HEIGHT = 792
    private val localeMx = Locale.Builder().setLanguage("es").setRegion("MX").build()
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", localeMx)

    @Throws(Exception::class)
    fun generateOrdenPdf(context: Context, data: OrdenConClienteYVehiculo, outFile: File): File {
        val pageWidth = PAGE_WIDTH
        val pageHeight = PAGE_HEIGHT
        val margin = 36f

        val doc = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = doc.startPage(pageInfo)
        val canvas = page.canvas

        // === PINTURAS ===
        val paintTitle = Paint().apply {
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.BLACK
            isAntiAlias = true
        }
        val paintHeading = Paint().apply {
            textSize = 11.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.BLACK
            isAntiAlias = true
        }
        val paintText = Paint().apply {
            textSize = 10f
            color = Color.BLACK
            isAntiAlias = true
        }
        val paintSmall = Paint().apply {
            textSize = 8.5f
            color = Color.DKGRAY
            isAntiAlias = true
        }
        val paintLine = Paint().apply {
            color = Color.BLACK
            strokeWidth = 1f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        val paintFill = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val paintX = Paint().apply {
            color = Color.BLACK
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        var y = margin

        // === ENCABEZADO TALLER ===
        canvas.drawText("TALLER DE MOTOS", margin, y, paintHeading)
        y += 14f
        canvas.drawText("~BOX HALACHÓ~", margin, y, paintHeading)
        y += 12f
        canvas.drawText("TELÉFONO: 999-333-68-77", margin, y, paintText)
        y += 10f
        canvas.drawText("DIRECCIÓN: CALLE 21 #30K ENTRE 10 Y 12 COL. SAN FRANCISCO", margin, y, paintText)

        y += 18f
        // Título centrado: ORDEN DE SERVICIO
        val title = "ORDEN DE SERVICIO"
        val titleX = (pageWidth / 2f) - (paintTitle.measureText(title) / 2f)
        canvas.drawText(title, titleX, y, paintTitle)
        y += 8f
        canvas.drawLine(margin, y, pageWidth - margin, y, paintLine)
        y += 14f

        // === DATOS DEL CLIENTE Y DATOS DE ORDEN (dos bloques con subdivisiones) ===
        val blockWidth = (pageWidth - margin * 2) / 2f
        val blockTop = y
        val blockHeight = 92f

        // Outer boxes
        canvas.drawRect(margin, blockTop, margin + blockWidth, blockTop + blockHeight, paintLine)
        canvas.drawRect(margin + blockWidth, blockTop, pageWidth - margin, blockTop + blockHeight, paintLine)

        // Titles inside boxes
        canvas.drawRect(margin, blockTop, margin + blockWidth, blockTop + 20f, paintLine)
        canvas.drawRect(margin + blockWidth, blockTop, pageWidth - margin, blockTop + 20f, paintLine)
        val leftTitleX = margin + (blockWidth / 2f) - (paintHeading.measureText("DATOS DEL CLIENTE") / 2f)
        canvas.drawText("DATOS DEL CLIENTE", leftTitleX, blockTop + 14f, paintHeading)
        val rightTitleX = margin + blockWidth + (blockWidth / 2f) - (paintHeading.measureText("DATOS DE ORDEN DE SERVICIO") / 2f)
        canvas.drawText("DATOS DE ORDEN DE SERVICIO", rightTitleX, blockTop + 14f, paintHeading)

        // Left block internal horizontal separators (3 rows)
        val leftColX = margin
        val leftColRight = margin + blockWidth
        val leftRow1 = blockTop + 20f
        val leftRow2 = leftRow1 + 26f
        val leftRow3 = leftRow2 + 26f
        canvas.drawLine(leftColX, leftRow1, leftColRight, leftRow1, paintLine)
        canvas.drawLine(leftColX, leftRow2, leftColRight, leftRow2, paintLine)
        canvas.drawLine(leftColX, leftRow3, leftColRight, leftRow3, paintLine)

        // ==================================================================
        // === INICIO DE SECCIÓN MODIFICADA ===
        // ==================================================================

        // Right block internal separators: create 3 rows for N° orden, ingreso, entrega
        val rightColX = margin + blockWidth
        val rightColRight = pageWidth - margin
        val rightRow1 = blockTop + 20f
        val rowDataHeight = (blockHeight - 20f) / 3f // (92-20)/3 = 24f
        val rightRow2 = rightRow1 + rowDataHeight
        val rightRow3 = rightRow2 + rowDataHeight
        canvas.drawLine(rightColX, rightRow1, rightColRight, rightRow1, paintLine)
        canvas.drawLine(rightColX, rightRow2, rightColRight, rightRow2, paintLine)
        canvas.drawLine(rightColX, rightRow3, rightColRight, rightRow3, paintLine)
        // La línea inferior (rightRow4) es dibujada por el rectángulo exterior del bloque

        // Vertical line in right block - ELIMINADA
        // val rightLabelWidth = 110f
        // canvas.drawLine(rightColX + rightLabelWidth, blockTop, rightColX + rightLabelWidth, blockTop + blockHeight, paintLine)

        // Fill client and order data text
        val cliente = data.cliente
        val orden = data.orden

        // Left side fields
        val nombre = cliente?.nombreCompleto ?: "—"
        val direccion = buildString {
            append(cliente?.calle ?: "")
            cliente?.calle1?.let { if (it.isNotBlank()) append(" e/ $it") }
            cliente?.calle2?.let { if (it.isNotBlank()) append(" y $it") }
            cliente?.numeroCasa?.let { if (it.isNotBlank()) append(" #$it") }
            cliente?.municipio?.let { if (it.isNotBlank()) append(", $it") }
            cliente?.estado?.let { if (it.isNotBlank()) append(", $it") }
        }.ifBlank { "—" }
        val celular = cliente?.telefono ?: "—"

        // Draw left labels on left column (labels bold)
        val labelOffset = 6f
        var ly = leftRow1 + 16f
        canvas.drawText("NOMBRE:", leftColX + labelOffset, ly, paintHeading)
        canvas.drawText(nombre, leftColX + 70f, ly, paintText)
        ly = leftRow2 + 16f
        canvas.drawText("DIRECCIÓN:", leftColX + labelOffset, ly, paintHeading)
        canvas.drawText(direccion, leftColX + 70f, ly, paintText)
        ly = leftRow3 + 16f
        canvas.drawText("NÚMERO CELULAR:", leftColX + labelOffset, ly, paintHeading)
        canvas.drawText(celular, leftColX + 110f, ly, paintText)

        // Right side fields - **MODIFICADO**
        val numeroOrden = orden.numeroOrden ?: "—"
        val fechaIngreso = try { dateFormatter.format(orden.fechaIngreso) } catch (_: Exception) { "—" }
        val fechaEntrega = orden.fechaEntregaEstimado?.let { dateFormatter.format(it) } ?: "—"

        val rightValueXOffset = 6f // Espacio desde el borde izquierdo de la celda
        val rightLabelYOffset = 13f // Posición Y para la etiqueta (relativa al inicio de la fila)
        val rightValueYOffset = 22f // Posición Y para el valor (relativa al inicio de la fila)

        // Fila 1: Número de Orden
        canvas.drawText("NÚMERO DE ORDEN:", rightColX + rightValueXOffset, rightRow1 + rightLabelYOffset, paintHeading)
        canvas.drawText(numeroOrden, rightColX + rightValueXOffset, rightRow1 + rightValueYOffset, paintText)

        // Fila 2: Fecha de Ingreso
        canvas.drawText("FECHA DE INGRESO:", rightColX + rightValueXOffset, rightRow2 + rightLabelYOffset, paintHeading)
        canvas.drawText(fechaIngreso, rightColX + rightValueXOffset, rightRow2 + rightValueYOffset, paintText)

        // Fila 3: Fecha de Entrega
        canvas.drawText("FECHA DE ENTREGA:", rightColX + rightValueXOffset, rightRow3 + rightLabelYOffset, paintHeading)
        canvas.drawText(fechaEntrega, rightColX + rightValueXOffset, rightRow3 + rightValueYOffset, paintText)

        // Move y below the blocks
        y = blockTop + blockHeight + 18f

        // ==================================================================
        // === FIN DE SECCIÓN MODIFICADA ===
        // ==================================================================

        // === DATOS DEL VEHÍCULO (tabla con celdas) ===
        canvas.drawText("DATOS DEL VEHÍCULO", margin, y, paintHeading)
        y += 8f
        val vehicleTop = y
        val columns = listOf("MARCA", "MODELO", "AÑO", "COLOR", "PLACAS", "VIN")
        val veh = data.vehiculo
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
        val vehColWidth = vehTableWidth / columns.size
        // header row box
        var vx = vehTableLeft
        val headerTop = vehicleTop
        val headerBottom = headerTop + 18f
        for (c in columns) {
            canvas.drawRect(vx, headerTop, vx + vehColWidth, headerBottom, paintLine)
            val tx = vx + 4f
            val ty = headerTop + 14f
            canvas.drawText(c, tx, ty, paintHeading)
            vx += vehColWidth
        }
        // values row
        val valuesTop = headerBottom
        val valuesBottom = valuesTop + 20f
        vx = vehTableLeft
        for (v in vehValues) {
            canvas.drawRect(vx, valuesTop, vx + vehColWidth, valuesBottom, paintLine)
            canvas.drawText(v, vx + 4f, valuesTop + 14f, paintText)
            vx += vehColWidth
        }

        y = valuesBottom + 18f

        // === DESCRIPCIÓN DE FALLA (cuadro) ===
        canvas.drawText("DESCRIPCIÓN DE FALLA", margin, y, paintHeading)
        y += 6f
        val descTop = y
        val descHeight = 48f
        canvas.drawRect(margin, descTop, pageWidth - margin, descTop + descHeight, paintLine)
        val falla = orden.descripcionFalla ?: "—"
        drawMultilineText(canvas, falla, margin + 6f, descTop + 14f, pageWidth - margin * 2 - 12f, paintText)
        y = descTop + descHeight + 18f

        // === CONDICIONES DEL VEHÍCULO (dos columnas) ===
        canvas.drawText("CONDICIONES DEL VEHÍCULO", margin, y, paintHeading)
        y += 8f

        val condiciones = orden.condiciones

        // Tabla: dos columnas (lista izquierda y lista derecha)
        val itemsLeft = listOf(
            "Espejos", "Asientos", "Faro delantero", "Luz de paro", "Trasero",
            "Direccionales", "Cubiertas", "Completas", "Tapón de gasolina"
        )
        val itemsRight = listOf(
            "Pedales", "Parabrisas", "Claxon", "Tapón de aceite", "Tapón radiador",
            "Filtro de aire", "Batería", "Llaves"
        )

        val condStartY = y
        val condRowHeight = 18f
        val condColGap = 12f
        val condColWidth = (pageWidth - margin * 2 - condColGap) / 2f

        // For each column, draw header row (labels) then rows with 4 cells: PRESENTA | SI | NO | ROTO/DAÑADO
        val labels = listOf("PRESENTA", "SI", "NO", "ROTO/DAÑADO")
        val labelWidths = floatArrayOf(condColWidth * 0.45f, condColWidth * 0.16f, condColWidth * 0.16f, condColWidth * 0.23f)

        // Draw header labels for left column
        var headerX = margin
        var headerY = condStartY
        for (i in labels.indices) {
            val w = labelWidths[i]
            canvas.drawRect(headerX, headerY, headerX + w, headerY + condRowHeight, paintLine)
            canvas.drawText(labels[i], headerX + 4f, headerY + 13f, paintSmall)
            headerX += w
        }
        // Draw header labels for right column
        headerX = margin + condColWidth + condColGap
        for (i in labels.indices) {
            val w = labelWidths[i]
            canvas.drawRect(headerX, headerY, headerX + w, headerY + condRowHeight, paintLine)
            canvas.drawText(labels[i], headerX + 4f, headerY + 13f, paintSmall)
            headerX += w
        }

        // Function to get textual value from condición property (supports Enum or String)
        fun condToString(value: Any?): String? {
            return when (value) {
                null -> null
                is Enum<*> -> value.name
                else -> value.toString()
            }
        }

        // Helper to get the string value for a named item from condiciones object.
        // IMPORTANT: these property names must exist in your model (ajusta si cambian).
        fun getCondValueByName(name: String): String? {
            try {
                // Acceso directo a propiedades (usa el nombre de la propiedad tal como existe en tu modelo)
                return when (name) {
                    "Espejos" -> condToString(condiciones?.espejos)
                    "Asientos" -> condToString(condiciones?.asientos)
                    "Faro delantero" -> condToString(condiciones?.faroDelantero)
                    "Luz de paro" -> condToString(condiciones?.luzTrasera)
                    "Trasero" -> condToString(condiciones?.luzTrasera)
                    "Direccionales" -> condToString(condiciones?.direccionales)
                    "Cubiertas" -> condToString(condiciones?.cubiertas)
                    "Completas" -> condToString(condiciones?.cubiertas)
                    "Tapón de gasolina" -> condToString(condiciones?.taponGasolina)
                    "Pedales" -> condToString(condiciones?.pedales)
                    "Parabrisas" -> condToString(condiciones?.parabrisas)
                    "Claxon" -> condToString(condiciones?.claxon)
                    "Tapón de aceite" -> condToString(condiciones?.taponAceite)
                    "Tapón radiador" -> condToString(condiciones?.taponRadiador)
                    "Filtro de aire" -> condToString(condiciones?.filtroAire)
                    "Batería" -> condToString(condiciones?.bateria)
                    "Llaves" -> condToString(condiciones?.llaves)
                    else -> null
                }
            } catch (ex: Exception) {
                return null
            }
        }

        // Draw rows left column
        var rowY = condStartY + condRowHeight
        for (item in itemsLeft) {
            var colX = margin
            // draw 4 cells
            for (i in 0 until 4) {
                val w = labelWidths[i]
                canvas.drawRect(colX, rowY, colX + w, rowY + condRowHeight, paintLine)
                if (i == 0) {
                    // item label
                    canvas.drawText(item, colX + 4f, rowY + 13f, paintText)
                }
                colX += w
            }

            // mark X if needed
            val raw = getCondValueByName(item)?.lowercase(Locale.getDefault()) ?: ""
            val markCol = when {
                raw.contains("si") -> 1
                raw.contains("no") -> 2
                raw.contains("dañ") || raw.contains("roto") -> 3
                else -> -1
            }
            if (markCol >= 1) {
                val xMark = margin + labelWidths.take(markCol).sum() + (labelWidths[markCol] / 2f) - 4f
                val yMark = rowY + condRowHeight - 6f
                canvas.drawText("X", xMark, yMark, paintX)
            }

            rowY += condRowHeight
        }

        // Draw rows right column
        rowY = condStartY + condRowHeight
        for (item in itemsRight) {
            var colX = margin + condColWidth + condColGap
            for (i in 0 until 4) {
                val w = labelWidths[i]
                canvas.drawRect(colX, rowY, colX + w, rowY + condRowHeight, paintLine)
                if (i == 0) {
                    canvas.drawText(item, colX + 4f, rowY + 13f, paintText)
                }
                colX += w
            }

            // mark X if needed
            val raw = getCondValueByName(item)?.lowercase(Locale.getDefault()) ?: ""
            val markCol = when {
                raw.contains("si") -> 1
                raw.contains("no") -> 2
                raw.contains("dañ") || raw.contains("roto") -> 3
                else -> -1
            }
            if (markCol >= 1) {
                val xMark = margin + condColWidth + condColGap + labelWidths.take(markCol).sum() + (labelWidths[markCol] / 2f) - 4f
                val yMark = rowY + condRowHeight - 6f
                canvas.drawText("X", xMark, yMark, paintX)
            }

            rowY += condRowHeight
        }

        // Advance y below both columns
        y = rowY + 12f

        // Observaciones (si existen)
        val observ = condiciones?.observaciones?.takeIf { it.isNotBlank() }
        if (!observ.isNullOrBlank()) {
            canvas.drawText("Observaciones:", margin, y, paintHeading)
            y += 12f
            y = drawMultilineText(canvas, observ, margin + 6f, y, pageWidth - margin * 2 - 12f, paintText)
            y += 8f
        }

        // === TEXTO LEGAL Y COSTO ===
        val costo = orden.costos?.costo ?: 0.0
        canvas.drawText("Costo de la revisión: $${String.format(Locale("es","MX"), "%.2f", costo)}", margin, y, paintHeading)
        y += 16f

        val legal = "En caso de que el presupuesto no sea aceptado, el consumidor deberá cubrir exclusivamente el costo de la revisión y diagnóstico. " +
                "El prestador del servicio se compromete a devolver la motocicleta en las mismas condiciones en las que fue entregada."
        y = drawMultilineText(canvas, legal, margin, y, pageWidth - margin * 2, paintSmall)
        y += 10f

        val clausula = "El consumidor: ( ) Acepta que el prestador del servicio pueda ceder o transmitir el vehículo, sus partes o piezas, a terceros (como torneros o soldadores) para fines de reparación. ( ) No acepta que el prestador de servicios utilice publicidad sobre bienes y servicios."
        y = drawMultilineText(canvas, clausula, margin, y, pageWidth - margin * 2, paintSmall)
        y += 18f

        // === FIRMAS ===
        val signY = y + 20f
        canvas.drawLine(margin, signY, margin + 220f, signY, paintLine)
        canvas.drawText("Firma del prestador de servicios", margin, signY + 14f, paintSmall)

        val consumerX = margin + 260f
        canvas.drawLine(consumerX, signY, consumerX + 220f, signY, paintLine)
        canvas.drawText("Firma de autorización del consumidor", consumerX, signY + 14f, paintSmall)

        // Draw signature image if exists
        orden.firmaClienteBase64?.takeIf { it.isNotBlank() }?.let { b64 ->
            try {
                val sigBitmap = base64ToBitmap(b64)
                val maxW = 180f
                val scale = minOf(maxW / sigBitmap.width, 1f)
                val sigW = (sigBitmap.width * scale).toInt()
                val sigH = (sigBitmap.height * scale).toInt()
                val dest = Rect(consumerX.toInt(), (signY - sigH).toInt(), (consumerX + sigW).toInt(), signY.toInt())
                canvas.drawBitmap(sigBitmap, null, dest, null)
            } catch (_: Exception) {}
        }

        // Final note
        val note = "NOTA: Al firmar este documento, el consumidor declara haber leído y aceptado las condiciones del servicio del taller."
        drawMultilineText(canvas, note, margin, signY + 40f, pageWidth - margin * 2, paintSmall)

        // Finish page and write
        doc.finishPage(page)
        FileOutputStream(outFile).use { out -> doc.writeTo(out) }
        doc.close()
        return outFile
    }

    // Helper: dibuja texto multilínea con ajuste de ancho
    private fun drawMultilineText(canvas: Canvas, text: String, startX: Float, startY: Float, maxWidth: Float, paint: Paint): Float {
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
                buffer = ""
            }
        }
        return currentY
    }

    private fun base64ToBitmap(base64: String): Bitmap {
        val decoded = Base64.decode(base64, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
    }

    fun generateAndGetUri(context: Context, data: OrdenConClienteYVehiculo, filename: String = "orden_servicio.pdf", providerAuthority: String): Uri {
        val outFile = File(context.cacheDir, filename)
        generateOrdenPdf(context, data, outFile)
        return FileProvider.getUriForFile(context, providerAuthority, outFile)
    }

    fun openPdf(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun sharePdf(context: Context, uri: Uri, chooserTitle: String = "Compartir PDF") {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(shareIntent, chooserTitle)
        context.startActivity(chooser)
    }
}
