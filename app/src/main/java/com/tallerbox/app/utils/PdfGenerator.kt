package com.tallerbox.app.utils

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Base64
import androidx.core.content.FileProvider
import com.tallerbox.app.R
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

        val paintX = Paint().apply {
            color = Color.BLACK
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        var y = margin

        // === ENCABEZADO TALLER ===

// === ENCABEZADO TALLER ===

        try {
            val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.moto)

            // Escalar el logo a un tamaño manejable
            val desiredWidth = 80f   // ancho deseado en puntos PDF (~1 pulgada)
            val aspectRatio = logoBitmap.height.toFloat() / logoBitmap.width
            val desiredHeight = desiredWidth * aspectRatio

            // Posición (arriba a la izquierda)
            val logoX = margin
            val logoY = margin // o puedes usar y - 40f si quieres que esté más arriba

            // Dibuja el logo ya escalado
            val destRect = RectF(logoX, logoY, logoX + desiredWidth, logoY + desiredHeight)
            canvas.drawBitmap(logoBitmap, null, destRect, null)

            // Texto al lado derecho del logo
            val textStartX = logoX + desiredWidth + 12f
            val textY = logoY + 12f
            canvas.drawText("TALLER DE MOTOS", textStartX, textY, paintHeading)
            canvas.drawText("~BOX HALACHÓ~", textStartX, textY + 14f, paintHeading)
            canvas.drawText("TELÉFONO: 999-333-68-77", textStartX, textY + 28f, paintText)
            canvas.drawText("DIRECCIÓN: CALLE 21 #30K ENTRE 10 Y 12 COL. SAN FRANCISCO", textStartX, textY + 42f, paintText)

            // Avanza el cursor de escritura después del encabezado
            y = logoY + desiredHeight + 20f

        } catch (e: Exception) {
            // Fallback si hay error al cargar el logo
            canvas.drawText("TALLER DE MOTOS", margin, y, paintHeading)
            y += 14f
            canvas.drawText("~BOX HALACHÓ~", margin, y, paintHeading)
            y += 12f
            canvas.drawText("TELÉFONO: 999-333-68-77", margin, y, paintText)
            y += 10f
            canvas.drawText("DIRECCIÓN: CALLE 21 #30K ENTRE 10 Y 12 COL. SAN FRANCISCO", margin, y, paintText)
            y += 18f
        }


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
        val rowClientHeight = (blockHeight - 20f) / 3f // 24f
        val leftRow2 = leftRow1 + rowClientHeight
        val leftRow3 = leftRow2 + rowClientHeight
        canvas.drawLine(leftColX, leftRow1, leftColRight, leftRow1, paintLine)
        canvas.drawLine(leftColX, leftRow2, leftColRight, leftRow2, paintLine)
        canvas.drawLine(leftColX, leftRow3, leftColRight, leftRow3, paintLine)

        // Right block internal separators (3 rows)
        val rightColX = margin + blockWidth
        val rightColRight = pageWidth - margin
        val rightRow1 = blockTop + 20f
        val rowDataHeight = (blockHeight - 20f) / 3f // 24f
        val rightRow2 = rightRow1 + rowDataHeight
        val rightRow3 = rightRow2 + rowDataHeight
        canvas.drawLine(rightColX, rightRow1, rightColRight, rightRow1, paintLine)
        canvas.drawLine(rightColX, rightRow2, rightColRight, rightRow2, paintLine)
        canvas.drawLine(rightColX, rightRow3, rightColRight, rightRow3, paintLine)

        // ==================================================================
        // === INICIO DE CAMBIOS (Ajustes de ancho y línea en CLIENTE) ===
        // ==================================================================

        // **CAMBIO 1: Ajustar ancho de etiqueta para ORDEN DE SERVICIO**
        // Aumentar rightLabelWidth de 110f a 130f para dar más espacio a las etiquetas largas
        val rightLabelWidth = 130f
        canvas.drawLine(rightColX + rightLabelWidth, blockTop + 20f, rightColX + rightLabelWidth, blockTop + blockHeight, paintLine)

        // **CAMBIO 2: Añadir la línea vertical en DATOS DEL CLIENTE**
        // Usaremos el mismo ancho para la etiqueta del cliente para mantener la simetría
        val clientLabelWidth = 130f
        canvas.drawLine(leftColX + clientLabelWidth, blockTop + 20f, leftColX + clientLabelWidth, blockTop + blockHeight, paintLine)


        // === Llenado de Datos ===

        val cliente = data.cliente
        val orden = data.orden

        // Left side fields (Cliente)
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

        // Draw left labels and values
        val labelOffset = 6f
        val verticalOffset = 16f // Centrado vertical para filas de 24f

        val clientValueX = leftColX + clientLabelWidth + labelOffset
        val clientValueMaxWidth = blockWidth - clientLabelWidth - 12f // 6f padding izq/der

        var ly = leftRow1 + verticalOffset
        canvas.drawText("NOMBRE:", leftColX + labelOffset, ly, paintHeading)
        // **Ajustado**: Usar multilínea con offset Y ajustado y ancho fijo
        drawMultilineText(canvas, nombre, clientValueX, leftRow1 + verticalOffset - 2f, clientValueMaxWidth, paintText)


        ly = leftRow2 + verticalOffset
        canvas.drawText("DIRECCIÓN:", leftColX + labelOffset, ly, paintHeading)
        // **Ajustado**: Usar multilínea con offset Y ajustado y ancho fijo
        drawMultilineText(canvas, direccion, clientValueX, leftRow2 + verticalOffset - 2f, clientValueMaxWidth, paintText)

        ly = leftRow3 + verticalOffset
        canvas.drawText("NÚMERO CELULAR:", leftColX + labelOffset, ly, paintHeading)
        // **Ajustado**: Usar multilínea con offset Y ajustado y ancho fijo
        drawMultilineText(canvas, celular, clientValueX, leftRow3 + verticalOffset - 2f, clientValueMaxWidth, paintText)


        // Right side fields (Orden de Servicio)
        val numeroOrden = orden.numeroOrden ?: "—"
        val fechaIngreso = try { dateFormatter.format(orden.fechaIngreso) } catch (_: Exception) { "—" }
        val fechaEntrega = orden.fechaEntregaEstimado?.let { dateFormatter.format(it) } ?: "—"

        val rightLabelX = rightColX + 6f
        val rightValueX = rightColX + rightLabelWidth + 6f
        val rightValueMaxWidth = blockWidth - rightLabelWidth - 12f

        // Fila 1: Número de Orden
        var ry = rightRow1 + verticalOffset
        canvas.drawText("NÚMERO DE ORDEN:", rightLabelX, ry, paintHeading)
        drawMultilineText(canvas, numeroOrden, rightValueX, rightRow1 + verticalOffset - 2f, rightValueMaxWidth, paintText)

        // Fila 2: Fecha de Ingreso
        ry = rightRow2 + verticalOffset
        canvas.drawText("FECHA DE INGRESO:", rightLabelX, ry, paintHeading)
        drawMultilineText(canvas, fechaIngreso, rightValueX, rightRow2 + verticalOffset - 2f, rightValueMaxWidth, paintText)

        // Fila 3: Fecha de Entrega
        ry = rightRow3 + verticalOffset
        canvas.drawText("FECHA DE ENTREGA:", rightLabelX, ry, paintHeading)
        drawMultilineText(canvas, fechaEntrega, rightValueX, rightRow3 + verticalOffset - 2f, rightValueMaxWidth, paintText)

        // Move y below the blocks
        y = blockTop + blockHeight + 18f

        // ==================================================================
        // === FIN DE CAMBIOS ===
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

        // ** Anchos de columna personalizados **
        // Total = 1.0
        val colWidths = floatArrayOf(
            vehTableWidth * 0.17f, // MARCA
            vehTableWidth * 0.17f, // MODELO
            vehTableWidth * 0.10f, // AÑO (Corto)
            vehTableWidth * 0.16f, // COLOR
            vehTableWidth * 0.12f, // PLACAS (Corto)
            vehTableWidth * 0.28f  // VIN (Largo)
        )

        // header row box
        var vx = vehTableLeft
        val headerTop = vehicleTop
        val headerBottom = headerTop + 18f
        // Loop por índice para usar anchos personalizados
        for (i in columns.indices) {
            val c = columns[i]
            val w = colWidths[i]
            canvas.drawRect(vx, headerTop, vx + w, headerBottom, paintLine)
            val tx = vx + 4f
            val ty = headerTop + 14f
            canvas.drawText(c, tx, ty, paintHeading)
            vx += w
        }

        // values row
        val valuesTop = headerBottom
        val valuesBottom = valuesTop + 20f
        vx = vehTableLeft
        // Loop por índice para usar anchos personalizados
        for (i in vehValues.indices) {
            val v = vehValues[i]
            val w = colWidths[i]
            canvas.drawRect(vx, valuesTop, vx + w, valuesBottom, paintLine)
            // ** Se mantiene: Usar multilínea para todos los valores **
            val valueMaxWidth = w - 8f // 4f padding izq/der
            drawMultilineText(canvas, v, vx + 4f, valuesTop + 14f, valueMaxWidth, paintText)
            vx += w
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
        val headerY = condStartY
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
                // *** Se agregaron safe calls (?.) para evitar crashes si 'condiciones' es null ***
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
        var rowYLeft = condStartY + condRowHeight // Rastreador de 'y' para la columna izquierda
        for (item in itemsLeft) {
            var colX = margin
            // draw 4 cells
            for (i in 0 until 4) {
                val w = labelWidths[i]
                canvas.drawRect(colX, rowYLeft, colX + w, rowYLeft + condRowHeight, paintLine)
                if (i == 0) {
                    // item label
                    canvas.drawText(item, colX + 4f, rowYLeft + 13f, paintText)
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
                val yMark = rowYLeft + condRowHeight - 6f
                canvas.drawText("X", xMark, yMark, paintX)
            }

            rowYLeft += condRowHeight
        }

        // Draw rows right column
        var rowYRight = condStartY + condRowHeight // Rastreador de 'y' para la columna derecha
        for (item in itemsRight) {
            var colX = margin + condColWidth + condColGap
            for (i in 0 until 4) {
                val w = labelWidths[i]
                canvas.drawRect(colX, rowYRight, colX + w, rowYRight + condRowHeight, paintLine)
                if (i == 0) {
                    canvas.drawText(item, colX + 4f, rowYRight + 13f, paintText)
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
                val yMark = rowYRight + condRowHeight - 6f
                canvas.drawText("X", xMark, yMark, paintX)
            }

            rowYRight += condRowHeight
        }

        // Advance y below both columns
        y = maxOf(rowYLeft, rowYRight) + 12f // Usa el valor 'y' MÁXIMO de ambas columnas


        // **INICIO DE CAMBIO DE TEXTO LEGAL Y FIRMAS**

        // 1. Eliminar Observaciones (si existen) y el texto legal anterior:
        // Se borra el bloque que dibuja "Observaciones:"

        // 2. Dibujar el primer párrafo legal (usando paintText)
        val legal1 = "En caso de que el presupuesto no sea aceptado, el consumidor deberá cubrir exclusivamente el costo de la revisión y\n" +
                "diagnóstico. El prestador del servicio se compromete a devolver la motocicleta en las mismas condiciones en las que fue\n" +
                "entregada, salvo las consecuencias inevitables derivadas del diagnóstico."
        y = drawMultilineText(canvas, legal1, margin, y, pageWidth - margin * 2, paintText)
        y += 12f

        // 3. Costo de la revisión y líneas de firma del prestador
        val costo = orden.costos?.costo ?: 0.0
        val costoStr = String.format(Locale("es","MX"), "%.2f", costo)

        // Costo de la revisión
        canvas.drawText("Costo de la revisión: $$costoStr", margin, y, paintText)
        // Dibujar línea de firma del prestador (se usa el mismo estilo que la otra firma, pero se ajusta)
        val lineLength = 220f
        val lineY1 = y + 16f
        canvas.drawLine(margin, lineY1, margin + lineLength, lineY1, paintLine)
        canvas.drawText("Firma del prestador de servicios:", margin, lineY1 + 14f, paintText)

        // Fecha (al lado de la firma del prestador)
        val fechaX = margin + lineLength + 20f
        canvas.drawLine(fechaX, lineY1, fechaX + 150f, lineY1, paintLine)
        canvas.drawText("Fecha: ${dateFormatter.format(Date())}", fechaX, lineY1 + 14f, paintText)
        y = lineY1 + 28f

        // 4. Segundo párrafo legal/cláusula (más largo, usar paintSmall)
        val clausulaNueva = "El consumidor: ( ) Acepta que el prestador del servicio pueda ceder o transmitir el vehículo, sus partes o piezas, a terceros " +
                "(como torneros, soldadores u otros especialistas), ya sea con fines de reparación o para la obtención de cotizaciones de " +
                "costos y precios. Esto será permitido únicamente en caso de ser estrictamente necesario y siempre que el propietario sea " +
                "previamente informado de estas acciones y haya dado su consentimiento para el traslado del vehículo o de sus componentes."
        y = drawMultilineText(canvas, clausulaNueva, margin, y, pageWidth - margin * 2, paintSmall)
        y += 8f

        // 5. Cláusula de publicidad
        val publicidad = "( ) Acepta ( ) No acepta que el prestador de servicios envíe publicidad sobre bienes y servicios."
        y = drawMultilineText(canvas, publicidad, margin, y, pageWidth - margin * 2, paintSmall)
        y += 18f

        // 6. Firma del consumidor y Nota Importante
        val signY = y + 20f

        // Firma de autorización del consumidor (más larga, centrada o en el lado derecho)
        val consumerX = margin + (pageWidth - margin * 2 - lineLength) // Iniciar más a la derecha para no chocar
        val consumerLineWidth = pageWidth - margin - consumerX
        canvas.drawLine(consumerX, signY, pageWidth - margin, signY, paintLine)
        canvas.drawText("Firma de autorización del consumidor:", consumerX, signY + 14f, paintText)

        // Draw signature image if exists
        orden.firmaClienteBase64?.takeIf { it.isNotBlank() }?.let { b64 ->
            try {
                val sigBitmap = base64ToBitmap(b64)
                val maxW = 180f
                val scale = minOf(maxW / sigBitmap.width, 1f)
                val sigW = (sigBitmap.width * scale).toInt()
                val sigH = (sigBitmap.height * scale).toInt()
                // Centrar firma sobre la línea
                val sigDrawX = consumerX + (consumerLineWidth / 2f) - (sigW / 2f)
                val dest = Rect(sigDrawX.toInt(), (signY - sigH).toInt(), (sigDrawX + sigW).toInt(), signY.toInt())
                canvas.drawBitmap(sigBitmap, null, dest, null)
            } catch (_: Exception) {}
        }

        y = signY + 30f

        // Final note (NOTA IMPORTANTE)
        val note = "NOTA IMPORTANTE: Al firmar este documento, el consumidor declara haber leído y estar de acuerdo con el reglamento\n" +
                "interno del taller mecánico, del cual ha sido informado previamente."
        y = drawMultilineText(canvas, note, margin, y, pageWidth - margin * 2, paintSmall.apply { typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) })

        // **FIN DE CAMBIO DE TEXTO LEGAL Y FIRMAS**


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