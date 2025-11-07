package com.tallerbox.app.utils

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Base64
import android.util.Log.w
import androidx.core.content.FileProvider
import com.tallerbox.app.R
import com.tallerbox.app.model.orden.OrdenConClienteYVehiculo
import com.tallerbox.app.model.orden.CondicionVehiculo
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min

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

        // === PINTURAS (REDISEÑADAS SEGÚN TUS PETICIONES) ===

        // CAMBIO: Fuente 14pt (Bold) para Título del Taller
        val paintHeaderTitle = Paint().apply {
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.BLACK
            isAntiAlias = true
        }
        // CAMBIO: Fuente 10pt (Regular) para Info del Taller
        val paintHeaderInfo = Paint().apply {
            textSize = 10f
            color = Color.BLACK
            isAntiAlias = true
        }
        // CAMBIO: Fuente 11pt (Bold) para Título de Sección "ORDEN DE SERVICIO"
        val paintSectionTitle = Paint().apply {
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.BLACK
            isAntiAlias = true
        }
        // CAMBIO: Fuente 10pt (Bold) para Títulos de Cajas (Datos Cliente, Vehículo, etc.)
        val paintBoxTitle = Paint().apply {
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.BLACK
            isAntiAlias = true
        }
        // CAMBIO: Fuente 8pt (Bold) para Cabeceras de Tablas (MARCA, SI, NO, etc.)
        val paintTableHeader = Paint().apply {
            textSize = 8f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.DKGRAY
            isAntiAlias = true
        }
        // CAMBIO: Fuente 8pt (Regular) para todo el cuerpo de texto, datos y legales
        val paintBody = Paint().apply {
            textSize = 8f
            color = Color.BLACK
            isAntiAlias = true
        }
        // Sin cambios
        val paintLine = Paint().apply {
            color = Color.BLACK
            strokeWidth = 1f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        // CAMBIO: Fuente X más pequeña para que quepa en filas de 8pt
        val paintX = Paint().apply {
            color = Color.BLACK
            textSize = 10f // Reducido de 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        var y = margin


        // === ENCABEZADO TALLER ===
        try {
            val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.moto)
            val headerTop = margin
            val headerLeft = margin
            val headerRight = pageWidth - margin

            // --- PINTURA ---
            val paintTextHeaderInfo = android.text.TextPaint(paintHeaderInfo)

            // --- DIMENSIONES DEL LOGO (MANTENIDAS) ---
            val desiredLogoWidth = 100f
            val logoPadding = 5f
            val aspectRatio = logoBitmap.height.toFloat() / logoBitmap.width
            val finalLogoWidth = desiredLogoWidth
            val finalLogoHeight = finalLogoWidth * aspectRatio
            val logoAreaWidth = finalLogoWidth + logoPadding * 2
            val textStartX = headerLeft + logoAreaWidth + 10f
            val textMaxWidthForMeasurement = headerRight - textStartX - 5f

            // --- CÁLCULO DE ALTURA REAL DEL TEXTO ---

            val direccionTaller = "DIRECCIÓN: CALLE 21 #30K ENTRE 10 Y 12 COL. SAN FRANCISCO"
            val spacingBetweenTitleAndInfo = 6f
            val spacingBetweenInfoLines = 8f // <-- ¡Ajuste de separación aquí!

            // Font Metrics para cálculos precisos
            val fmTitle = paintHeaderTitle.fontMetrics
            val fmInfo = paintHeaderInfo.fontMetrics

            // 1. Calcular la altura total de la caja de texto (netTextContentHeight)
            var tempY = 0f
            // Títulos (TALLER DE MOTOS, ~BOX HALACHÓ~)
            tempY += (fmTitle.bottom - fmTitle.top) * 2 + spacingBetweenTitleAndInfo
            // TELÉFONO
            tempY += (fmInfo.bottom - fmInfo.top) + spacingBetweenInfoLines
            // Dirección Multilínea
            val staticLayout = android.text.StaticLayout.Builder.obtain(
                direccionTaller, 0, direccionTaller.length, paintTextHeaderInfo,
                textMaxWidthForMeasurement.toInt()
            )
                .setAlignment(android.text.Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1f)
                .setIncludePad(false)
                .build()
            val textHeightDirection = staticLayout.height.toFloat()
            tempY += textHeightDirection

            val netTextContentHeight = tempY
            val textBlockPadding = 8f
            val totalTextBlockHeight = netTextContentHeight + textBlockPadding

            // --- DETERMINAR ALTURA FINAL DEL ENCABEZADO ---
            val minLogoHeight = 80f
            val calculatedLogoHeight = max(minLogoHeight, finalLogoHeight)
            val contentHeight = max(calculatedLogoHeight + logoPadding * 2, totalTextBlockHeight)
            val headerBottom = headerTop + contentHeight

            val adjustedLogoHeight = min(finalLogoHeight, contentHeight - logoPadding * 2)
            val adjustedLogoWidth = adjustedLogoHeight / aspectRatio

            // --- DIBUJO DEL CONTORNO ---
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


            // --- DIBUJO DEL LOGO ---
            val logoX = headerLeft + logoPadding
            val logoY = headerTop + (contentHeight - adjustedLogoHeight) / 2f
            val logoDestRect = RectF(
                logoX,
                logoY,
                logoX + adjustedLogoWidth,
                logoY + adjustedLogoHeight
            )
            canvas.drawBitmap(logoBitmap, null, logoDestRect, null)


            // --- DIBUJO DEL TEXTO (FLUJO DE DIBUJO) ---

            var currentTextY = headerTop + (contentHeight - netTextContentHeight) / 2f

            // TALLER DE MOTOS
            canvas.drawText("TALLER DE MOTOS", textStartX, currentTextY + fmTitle.ascent.times(-1), paintHeaderTitle)
            currentTextY += fmTitle.bottom - fmTitle.top

            // ~BOX HALACHÓ~
            canvas.drawText("~BOX HALACHÓ~", textStartX, currentTextY + fmTitle.ascent.times(-1), paintHeaderTitle)
            currentTextY += (fmTitle.bottom - fmTitle.top) + spacingBetweenTitleAndInfo

            // TELÉFONO
            canvas.drawText("TELÉFONO: 999-333-68-77", textStartX, currentTextY + fmInfo.ascent.times(-1), paintHeaderInfo)
            currentTextY += (fmInfo.bottom - fmInfo.top) + spacingBetweenInfoLines // Ahora avanza 8f + altura de línea

            // DIRECCIÓN (multilínea)
            val finalYAfterDirection = drawMultilineText(canvas, direccionTaller, textStartX, currentTextY, textMaxWidthForMeasurement, paintHeaderInfo)

            // Ajustar el cursor 'y' para el siguiente bloque
            y = max(finalYAfterDirection, headerBottom) + 12f

        } catch (e: Exception) {
            w("PdfGenerator", "Error drawing logo/header", e)
            y = margin + 100f + 12f
        }



        // Título centrado: ORDEN DE SERVICIO
        val title = "ORDEN DE SERVICIO"
        val titleX = (pageWidth / 2f) - (paintSectionTitle.measureText(title) / 2f)
        canvas.drawText(title, titleX, y, paintSectionTitle)
        y += 6f
        canvas.drawLine(margin, y, pageWidth - margin, y, paintLine)
        y += 10f

        // === DATOS DEL CLIENTE Y DATOS DE ORDEN (dos bloques con subdivisiones) ===
        val blockWidth = (pageWidth - margin * 2) / 2f
        val blockTop = y
        val titleRowHeight = 16f

        val clientRow1Height = 16f
        val clientRow2Height = 32f
        val clientRow3Height = 16f
        val clientBlockHeight = titleRowHeight + clientRow1Height + clientRow2Height + clientRow3Height

        val orderBlockHeight = clientBlockHeight
        val orderRowHeight = (orderBlockHeight - titleRowHeight) / 3f

        // Outer boxes
        canvas.drawRect(margin, blockTop, margin + blockWidth, blockTop + clientBlockHeight, paintLine)
        canvas.drawRect(margin + blockWidth, blockTop, pageWidth - margin, blockTop + orderBlockHeight, paintLine)

        // Titles inside boxes
        canvas.drawRect(margin, blockTop, margin + blockWidth, blockTop + titleRowHeight, paintLine)
        canvas.drawRect(margin + blockWidth, blockTop, pageWidth - margin, blockTop + titleRowHeight, paintLine)

        val titleYOffset = 12f
        val leftTitleX = margin + (blockWidth / 2f) - (paintBoxTitle.measureText("DATOS DEL CLIENTE") / 2f)
        canvas.drawText("DATOS DEL CLIENTE", leftTitleX, blockTop + titleYOffset, paintBoxTitle)
        val rightTitleX = margin + blockWidth + (blockWidth / 2f) - (paintBoxTitle.measureText("DATOS DE ORDEN DE SERVICIO") / 2f)
        canvas.drawText("DATOS DE ORDEN DE SERVICIO", rightTitleX, blockTop + titleYOffset, paintBoxTitle)

        // Left block internal horizontal separators
        val leftColX = margin
        val leftColRight = margin + blockWidth
        val leftRow1 = blockTop + titleRowHeight
        val leftRow2 = leftRow1 + clientRow1Height
        val leftRow3 = leftRow2 + clientRow2Height
        canvas.drawLine(leftColX, leftRow2, leftColRight, leftRow2, paintLine)
        canvas.drawLine(leftColX, leftRow3, leftColRight, leftRow3, paintLine)

        // Right block internal separators
        val rightColX = margin + blockWidth
        val rightColRight = pageWidth - margin
        val rightRow1 = blockTop + titleRowHeight
        val rightRow2 = rightRow1 + orderRowHeight
        val rightRow3 = rightRow2 + orderRowHeight
        canvas.drawLine(rightColX, rightRow2, rightColRight, rightRow2, paintLine)
        canvas.drawLine(rightColX, rightRow3, rightColRight, rightRow3, paintLine)

        // Líneas verticales
        val rightLabelWidth = 130f
        canvas.drawLine(rightColX + rightLabelWidth, blockTop + titleRowHeight, rightColX + rightLabelWidth, blockTop + orderBlockHeight, paintLine)
        val clientLabelWidth = 130f
        canvas.drawLine(leftColX + clientLabelWidth, blockTop + titleRowHeight, leftColX + clientLabelWidth, blockTop + clientBlockHeight, paintLine)

        // === Llenado de Datos ===
        val cliente = data.cliente
        val orden = data.orden

        val nombre = cliente?.nombreCompleto ?: "—"
        // Lógica de Dirección (Asumo que está implementada)
        val direccion = "${cliente?.calle ?: ""} ${cliente?.numeroCasa ?: ""}, entre calles ${cliente?.calle1} y ${cliente?.calle2}, ${cliente?.municipio ?: ""}, ${cliente?.estado ?: ""}".trim().ifBlank { "—" }
        val celular = cliente?.telefono ?: "—"

        // Draw left labels and values
        val labelOffset = 6f
        val verticalOffsetStd = 12f
        val verticalOffsetDir = 12f

        val clientValueX = leftColX + clientLabelWidth + labelOffset
        val clientValueMaxWidth = blockWidth - clientLabelWidth - 12f

        // Fila 1 (Nombre)
        var ly = leftRow1 + verticalOffsetStd
        canvas.drawText("NOMBRE:", leftColX + labelOffset, ly, paintBoxTitle)
        drawMultilineText(canvas, nombre, clientValueX, leftRow1 + 10f, clientValueMaxWidth, paintBody)

        // Fila 2 (Dirección)
        ly = leftRow2 + verticalOffsetDir
        canvas.drawText("DIRECCIÓN:", leftColX + labelOffset, ly, paintBoxTitle)
        drawMultilineText(canvas, direccion, clientValueX, leftRow2 + 10f, clientValueMaxWidth, paintBody)

        // Fila 3 (Celular)
        ly = leftRow3 + verticalOffsetStd
        canvas.drawText("NÚMERO CELULAR:", leftColX + labelOffset, ly, paintBoxTitle)
        drawMultilineText(canvas, celular, clientValueX, leftRow3 + 10f, clientValueMaxWidth, paintBody)

        // Right side fields (Orden de Servicio)
        val numeroOrden = orden.numeroOrden ?: "—"
        val fechaIngreso = try { dateFormatter.format(orden.fechaIngreso) } catch (_: Exception) { "—" }
        val fechaEntrega = orden.fechaEntregaEstimado?.let { dateFormatter.format(it) } ?: "—"

        val rightLabelX = rightColX + 6f
        val rightValueX = rightColX + rightLabelWidth + 6f
        val rightValueMaxWidth = blockWidth - rightLabelWidth - 12f

        val rightVerticalOffset = (orderRowHeight / 2f) + 4f
        val rightTextYOffset = rightVerticalOffset - 4f

        // Fila 1: Número de Orden
        var ry = rightRow1 + rightVerticalOffset
        canvas.drawText("NÚMERO DE ORDEN:", rightLabelX, ry, paintBoxTitle)
        drawMultilineText(canvas, numeroOrden, rightValueX, rightRow1 + rightTextYOffset, rightValueMaxWidth, paintBody)

        // Fila 2: Fecha de Ingreso
        ry = rightRow2 + rightVerticalOffset
        canvas.drawText("FECHA DE INGRESO:", rightLabelX, ry, paintBoxTitle)
        drawMultilineText(canvas, fechaIngreso, rightValueX, rightRow2 + rightTextYOffset, rightValueMaxWidth, paintBody)

        // Fila 3: Fecha de Entrega
        ry = rightRow3 + rightVerticalOffset
        canvas.drawText("FECHA DE ENTREGA:", rightLabelX, ry, paintBoxTitle)
        drawMultilineText(canvas, fechaEntrega, rightValueX, rightRow3 + rightTextYOffset, rightValueMaxWidth, paintBody)

        // Move y below the blocks
        y = blockTop + clientBlockHeight + 12f

        // === DATOS DEL VEHÍCULO (tabla con celdas) ===
        canvas.drawText("DATOS DEL VEHÍCULO", margin, y, paintBoxTitle)
        y += 6f
        val vehicleTop = y
        val columns = listOf("MARCA", "MODELO", "AÑO", "COLOR", "PLACAS", "VIN")
        val veh = data.vehiculo

        // CORRECCIÓN: Definición de vehValues
        val vehValues = listOf(
            veh?.marca ?: "—",
            veh?.modelo ?: "—",
            veh?.ano.toString().takeIf { it != "null" } ?: "—",
            veh?.color ?: "—",
            veh?.placa ?: "—",
            veh?.vin ?: "—"
        )
        val vehTableLeft = margin
        val vehTableRight = pageWidth - margin
        val vehTableWidth = vehTableRight - vehTableLeft

        // CORRECCIÓN: Definición de colWidths
        val totalWidth = vehTableWidth
        val colWidths = floatArrayOf(
            totalWidth * 0.15f, totalWidth * 0.18f, totalWidth * 0.10f,
            totalWidth * 0.15f, totalWidth * 0.12f, totalWidth * 0.30f
        )

        val headerRowHeight = 16f
        val valueRowHeight = 16f
        val textYOffset = 12f

        // header row box
        var vx = vehTableLeft
        val headerTop = vehicleTop
        val headerBottom = headerTop + headerRowHeight
        for (i in columns.indices) {
            val c = columns[i]
            val w = colWidths[i]
            canvas.drawRect(vx, headerTop, vx + w, headerBottom, paintLine)
            val tx = vx + 4f
            val ty = headerTop + textYOffset
            canvas.drawText(c, tx, ty, paintTableHeader)
            vx += w
        }

        // values row
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

        y = valuesBottom + 12f

        // === DESCRIPCIÓN DE FALLA (cuadro) ===
        canvas.drawText("DESCRIPCIÓN DE FALLA", margin, y, paintBoxTitle)
        y += 6f
        val descTop = y
        val descHeight = 32f
        canvas.drawRect(margin, descTop, pageWidth - margin, descTop + descHeight, paintLine)
        val falla = orden.descripcionFalla ?: "—"
        drawMultilineText(canvas, falla, margin + 6f, descTop + 10f, pageWidth - margin * 2 - 12f, paintBody)
        y = descTop + descHeight + 12f

        // === CONDICIONES DEL VEHÍCULO (dos columnas) ===
        canvas.drawText("CONDICIONES DEL VEHÍCULO", margin, y, paintBoxTitle)
        y += 6f

        val condiciones = orden.condiciones

        // CORRECCIÓN: Definición de itemsLeft e itemsRight
        val itemsLeft = listOf(
            "Espejos", "Asientos", "Faro delantero", "Luz de paro", "Trasero",
            "Direccionales", "Cubiertas", "Completas", "Tapón de gasolina"
        )
        val itemsRight = listOf(
            "Pedales", "Parabrisas", "Claxon", "Tapón de aceite", "Tapón radiador",
            "Filtro de aire", "Batería", "Llaves"
        )

        val condStartY = y
        val condRowHeight = 16f
        val condColGap = 12f
        val condColWidth = (pageWidth - margin * 2 - condColGap) / 2f
        val labels = listOf("PRESENTA", "SI", "NO", "ROTO/DAÑADO")
        val labelWidths = floatArrayOf(condColWidth * 0.45f, condColWidth * 0.16f, condColWidth * 0.16f, condColWidth * 0.23f)
        val condTextYOffset = 12f
        val condHeaderYOffset = 12f

        // Draw header labels for left column
        var headerX = margin
        val headerY = condStartY
        for (i in labels.indices) {
            val w = labelWidths[i]
            canvas.drawRect(headerX, headerY, headerX + w, headerY + condRowHeight, paintLine)
            canvas.drawText(labels[i], headerX + 4f, headerY + condHeaderYOffset, paintTableHeader)
            headerX += w
        }
        // Draw header labels for right column
        headerX = margin + condColWidth + condColGap
        for (i in labels.indices) {
            val w = labelWidths[i]
            canvas.drawRect(headerX, headerY, headerX + w, headerY + condRowHeight, paintLine)
            canvas.drawText(labels[i], headerX + 4f, headerY + condHeaderYOffset, paintTableHeader)
            headerX += w
        }

        // Lógica para obtener el valor de la condición (asumo que se mapea el nombre a un campo)
        fun getCondValueByName(condiciones: CondicionVehiculo?, name: String): String {
            // 1. Obtener el valor del campo (que es un enum EstadoCondicion o null)
            val enumValue = when (name) {
                "Espejos" -> condiciones?.espejos
                "Asientos" -> condiciones?.asientos
                "Faro delantero" -> condiciones?.faroDelantero
                "Luz de trasera" -> condiciones?.luzTrasera
                "Direccionales" -> condiciones?.direccionales
                "Cubiertas" -> condiciones?.cubiertas
                "Tapón de gasolina" -> condiciones?.taponGasolina
                "Pedales" -> condiciones?.pedales
                "Parabrisas" -> condiciones?.parabrisas
                "Claxon" -> condiciones?.claxon
                "Tapón de aceite" -> condiciones?.taponAceite
                "Tapón radiador" -> condiciones?.taponRadiador
                "Filtro de aire" -> condiciones?.filtroAire
                "Batería" -> condiciones?.bateria
                "Llaves" -> condiciones?.llaves
                else -> null
            }

            // 2. Usar .name para convertir el ENUM (EstadoCondicion.SI) a String ("SI").
            // Si el valor es nulo, devuelve una cadena vacía.
            return enumValue?.name ?: ""
        }

        // Draw rows left column
        var rowYLeft = condStartY + condRowHeight
        for (item in itemsLeft) {
            var colX = margin

            // CORRECCIÓN: Obtener el valor de la condición y determinar markCol
            val condValue = getCondValueByName(condiciones, item)
            val markCol = when (condValue) {
                "SI" -> 1
                "NO" -> 2
                "ROTO_DANADO" -> 3
                else -> -1 // No se marca
            }

            for (i in 0 until 4) {
                val w = labelWidths[i]
                canvas.drawRect(colX, rowYLeft, colX + w, rowYLeft + condRowHeight, paintLine)
                if (i == 0) {
                    canvas.drawText(item, colX + 4f, rowYLeft + condTextYOffset, paintBody)
                }
                colX += w
            }

            // mark X if needed
            if (markCol >= 1) {
                // Cálculo de posición X para centrar la "X" en la columna markCol
                val xMark = margin + labelWidths.take(markCol).sum() + (labelWidths[markCol] / 2f) - (paintX.measureText("X") / 2f)
                val yMark = rowYLeft + condRowHeight - 4f
                canvas.drawText("X", xMark, yMark, paintX)
            }
            rowYLeft += condRowHeight
        }

        // Draw rows right column
        var rowYRight = condStartY + condRowHeight
        for (item in itemsRight) {
            var colX = margin + condColWidth + condColGap

            // CORRECCIÓN: Obtener el valor de la condición y determinar markCol
            val condValue = getCondValueByName(condiciones, item)
            val markCol = when (condValue) {
                "SI" -> 1
                "NO" -> 2
                "ROTO_DANADO" -> 3
                else -> -1 // No se marca
            }

            for (i in 0 until 4) {
                val w = labelWidths[i]
                canvas.drawRect(colX, rowYRight, colX + w, rowYRight + condRowHeight, paintLine)
                if (i == 0) {
                    canvas.drawText(item, colX + 4f, rowYRight + condTextYOffset, paintBody)
                }
                colX += w
            }
            // mark X if needed
            if (markCol >= 1) {
                // Cálculo de posición X para centrar la "X" en la columna markCol
                val xMark = margin + condColWidth + condColGap + labelWidths.take(markCol).sum() + (labelWidths[markCol] / 2f) - (paintX.measureText("X") / 2f)
                val yMark = rowYRight + condRowHeight - 4f
                canvas.drawText("X", xMark, yMark, paintX)
            }
            rowYRight += condRowHeight
        }

        y = maxOf(rowYLeft, rowYRight) + 10f

        // **INICIO DE CAMBIO DE TEXTO LEGAL Y FIRMAS**

        // 2. Dibujar el primer párrafo legal
        val legal1 = "En caso de que el presupuesto no sea aceptado, el consumidor deberá cubrir exclusivamente el costo de la revisión y diagnóstico. El prestador del servicio se compromete a devolver la motocicleta en las mismas condiciones en las que fue entregada, salvo las consecuencias inevitables derivadas del diagnóstico."
        y = drawMultilineText(canvas, legal1, margin, y, pageWidth - margin * 2, paintBody)
        y += 10f

        // 3. Costo de la revisión y líneas de firma del prestador
        val costo = orden.costos?.costo ?: 0.0
        val costoStr = String.format(Locale("es","MX"), "%.2f", costo)

        canvas.drawText("Costo de la revisión: $$costoStr", margin, y, paintBody)

        val lineLength = 220f
        val lineY1 = y + 12f
        canvas.drawLine(margin, lineY1, margin + lineLength, lineY1, paintLine)
        canvas.drawText("Firma del prestador de servicios:", margin, lineY1 + 10f, paintBody)

        // Fecha
        val fechaX = margin + lineLength + 20f
        canvas.drawLine(fechaX, lineY1, fechaX + 150f, lineY1, paintLine)
        canvas.drawText("Fecha: ${dateFormatter.format(Date())}", fechaX, lineY1 + 10f, paintBody)
        y = lineY1 + 24f

        // 4. Segundo párrafo legal/cláusula
        val clausulaNueva = "El consumidor: ( ) Acepta que el prestador del servicio pueda ceder o transmitir el vehículo, sus partes o piezas, a terceros (como torneros, soldadores u otros especialistas), ya sea con fines de reparación o para la obtención de cotizaciones de costos y precios. Esto será permitido únicamente en caso de ser estrictamente necesario y siempre que el propietario sea previamente informado de estas acciones y haya dado su consentimiento para el traslado del vehículo o de sus componentes."
        y = drawMultilineText(canvas, clausulaNueva, margin, y, pageWidth - margin * 2, paintBody)
        y += 6f

        // 5. Cláusula de publicidad
        val publicidad = "( ) Acepta ( ) No acepta que el prestador de servicios envíe publicidad sobre bienes y servicios."
        y = drawMultilineText(canvas, publicidad, margin, y, pageWidth - margin * 2, paintBody)
        y += 12f

        // 6. Firma del consumidor y Nota Importante
        val signY = y + 16f

        val consumerX = margin + (pageWidth - margin * 2 - lineLength)
        val consumerLineWidth = pageWidth - margin - consumerX
        canvas.drawLine(consumerX, signY, pageWidth - margin, signY, paintLine)
        canvas.drawText("Firma de autorización del consumidor:", consumerX, signY + 10f, paintBody)

        // Draw signature image if exists
        orden.firmaClienteBase64?.takeIf { it.isNotBlank() }?.let { b64 ->
            try {
                val sigBitmap = base64ToBitmap(b64)
                val maxW = 180f
                val scale = min(maxW / sigBitmap.width, 1f)
                val sigW = (sigBitmap.width * scale).toInt()
                val sigH = (sigBitmap.height * scale).toInt()
                val sigDrawX = consumerX + (consumerLineWidth / 2f) - (sigW / 2f)

                // === CAMBIO CLAVE DE LA FIRMA ===
                val overlap = 8f
                val dest = Rect(
                    sigDrawX.toInt(),
                    (signY - sigH + overlap).toInt(),
                    (sigDrawX + sigW).toInt(),
                    (signY + overlap).toInt()
                )
                // === FIN CAMBIO FIRMA ===

                canvas.drawBitmap(sigBitmap, null, dest, null)
            } catch (e: Exception) {
                w("PdfGenerator", "Error drawing signature", e)
            }
        }

        y = signY + 24f

        // Final note (NOTA IMPORTANTE)
        val note = "NOTA IMPORTANTE: Al firmar este documento, el consumidor declara haber leído y estar de acuerdo con el reglamento interno del taller mecánico, del cual ha sido informado previamente."
        val boldPaintBody = Paint(paintBody).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        y = drawMultilineText(canvas, note, margin, y, pageWidth - margin * 2, boldPaintBody)

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

    // Funciones adicionales
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