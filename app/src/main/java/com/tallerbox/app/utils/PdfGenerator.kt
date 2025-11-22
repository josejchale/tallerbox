package com.tallerbox.app.utils

import android.annotation.SuppressLint
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
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get
import androidx.core.graphics.set

object PdfGenerator {

    // Carta: 612 x 792 puntos
    private const val PAGE_WIDTH = 612
    private const val PAGE_HEIGHT = 792
    private val localeMx = Locale.Builder().setLanguage("es").setRegion("MX").build()
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", localeMx)

    private fun resizeSignatureBase64(base64: String, maxWidth: Int = 800): Bitmap? {
        return try {
            // Decodificar Base64
            val decodedBytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
            val original = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size) ?: return null

            // Si ya es pequeña, no hacemos nada
            if (original.width <= maxWidth) {
                return original.copy(Bitmap.Config.ARGB_8888, true)
            }

            // Escalado manteniendo proporción
            val scale = maxWidth.toFloat() / original.width
            val newHeight = (original.height * scale).toInt()

            Bitmap.createScaledBitmap(original, maxWidth, newHeight, true)
        } catch (e: Exception) {
            null
        }
    }

    private fun removeWhiteBackground(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // Copia ARGB para permitir transparencia
        val result = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Arreglo donde cargamos todos los píxeles de una sola vez
        val pixels = IntArray(width * height)
        result.getPixels(pixels, 0, width, 0, 0, width, height)

        // Umbral para detectar blanco (tunable)
        val threshold = 240

        for (i in pixels.indices) {
            val color = pixels[i]

            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)

            // Si es casi blanco → transparente
            if (r > threshold && g > threshold && b > threshold) {
                pixels[i] = Color.TRANSPARENT
            }
        }

        // Escribimos todos los píxeles optimizados de vuelta de un solo golpe
        result.setPixels(pixels, 0, width, 0, 0, width, height)

        return result
    }

    private fun preprocessSignature(base64: String): Bitmap? {
        val resized = resizeSignatureBase64(base64) ?: return null
        return removeWhiteBackground(resized)
    }



    private fun drawRichMultilineText(
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

            // Si la línea está vacía, saltar renglón
            if (line.isBlank()) {
                y += normalPaint.textSize * 1.5f
                continue
            }

            // Dividir en segmentos normales y en negritas **texto**
            val parts = Regex("(\\*\\*.*?\\*\\*)|([^*]+)").findAll(line)

            var x = startX

            for (p in parts) {
                val segment = p.value

                // Detectar negritas
                val isBold = segment.startsWith("**") && segment.endsWith("**")
                val cleanText = if (isBold) segment.substring(2, segment.length - 2) else segment

                val paint = if (isBold) boldPaint else normalPaint

                val words = cleanText.split(" ")
                for (word in words) {
                    val wordText = "$word "
                    val wordWidth = paint.measureText(wordText)

                    // Si no cabe → salto de línea
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

    @SuppressLint("UseKtx")
    @Throws(Exception::class)
    fun generateOrdenPdf(
        context: Context,
        data: OrdenConClienteYVehiculo,
        outFile: File,
        firmaBase64: String? = null
    ): File {
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
        // CAMBIO: Fuente 8pt (Regular) para
        val paintBody = Paint().apply {
            color = Color.BLACK
            textSize = 10f
            isAntiAlias = true
        }

        val paintBoldBody = Paint().apply {
            color = Color.BLACK
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
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
        y += 16f

// === DATOS DEL CLIENTE Y DATOS DE ORDEN (dos bloques con subdivisiones) ===
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

// === Fondo gris para encabezados ===
        val headerFillPaint = Paint().apply {
            style = Paint.Style.FILL
            color = Color.LTGRAY
        }
        // Borde del encabezado "DATOS DEL CLIENTE"
        canvas.drawRect(leftColX, blockTop, leftColRight, blockTop + titleRowHeight, paintLine)
        // Borde del encabezado "DATOS DE ORDEN DE SERVICIO"
        canvas.drawRect(rightColX, blockTop, rightColRight, blockTop + titleRowHeight, paintLine)
        canvas.drawRect(leftColX, blockTop, leftColRight, blockTop + titleRowHeight, headerFillPaint)
        canvas.drawRect(rightColX, blockTop, rightColRight, blockTop + titleRowHeight, headerFillPaint)

// === Contornos de los bloques ===
        canvas.drawRect(leftColX, blockTop, leftColRight, blockTop + titleRowHeight, paintLine)
        canvas.drawRect(leftColX, blockTop, leftColRight, blockTop + clientBlockHeight, paintLine)
        canvas.drawRect(rightColX, blockTop, rightColRight, blockTop + orderBlockHeight, paintLine)

// === Títulos de los bloques ===
        val titleYOffset = 12f
        val leftTitleX = leftColX + (blockWidth / 2f) - (paintBoxTitle.measureText("DATOS DEL CLIENTE") / 2f)
        canvas.drawText("DATOS DEL CLIENTE", leftTitleX, blockTop + titleYOffset, paintBoxTitle)
        canvas.drawRect(leftColX, blockTop, leftColRight, blockTop + titleRowHeight, paintLine)

        val rightTitleX = rightColX + (blockWidth / 2f) - (paintBoxTitle.measureText("DATOS DE ORDEN DE SERVICIO") / 2f)
        canvas.drawText("DATOS DE ORDEN DE SERVICIO", rightTitleX, blockTop + titleYOffset, paintBoxTitle)
        canvas.drawRect(leftColX, blockTop, leftColRight, blockTop + titleRowHeight, paintLine)

// === Separadores horizontales internos ===
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

// === Líneas verticales internas ===
        val rightLabelWidth = 130f
        canvas.drawLine(rightColX + rightLabelWidth, blockTop + titleRowHeight, rightColX + rightLabelWidth, blockTop + orderBlockHeight, paintLine)
        val clientLabelWidth = 130f
        canvas.drawLine(leftColX + clientLabelWidth, blockTop + titleRowHeight, leftColX + clientLabelWidth, blockTop + clientBlockHeight, paintLine)

// === Llenado de Datos ===
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

// === Datos de la orden ===
        val numeroOrden = orden.numeroOrden
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
        y = blockTop + clientBlockHeight + 20f


// === DATOS DEL VEHÍCULO (tabla con celdas) ===
        val veh = data.vehiculo
        val columns = listOf("MARCA", "MODELO", "AÑO", "COLOR", "PLACAS", "VIN")
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

        val colWidths = floatArrayOf(
            vehTableWidth * 0.15f, // MARCA
            vehTableWidth * 0.15f, // MODELO
            vehTableWidth * 0.10f, // AÑO
            vehTableWidth * 0.15f, // COLOR
            vehTableWidth * 0.12f, // PLACAS
            vehTableWidth * 0.33f  // VIN (más ancho)
        )

        val headerRowHeight = 16f
        val valueRowHeight = 16f
        val textYOffset = 12f

// ===  DATOS DEL VEHÍCULO ===
        val vehicleTitlePaint = Paint().apply {
            style = Paint.Style.FILL
            color = Color.LTGRAY
        }
        canvas.drawRect(vehTableLeft, y, vehTableRight, y + headerRowHeight, vehicleTitlePaint)
        canvas.drawRect(vehTableLeft, y, vehTableRight, y + headerRowHeight, paintLine)
        val titleTextX = vehTableLeft + (vehTableWidth / 2f) - (paintBoxTitle.measureText("DATOS DEL VEHÍCULO") / 2f)
        canvas.drawText("DATOS DEL VEHÍCULO", titleTextX, y + textYOffset, paintBoxTitle)
        y += headerRowHeight

// === Tabla de datos del vehículo ===
        val headerTop = y
        val headerBottom = headerTop + headerRowHeight
        var vx = vehTableLeft

        for (i in columns.indices) {
            val c = columns[i]
            val w = colWidths[i]
            val cellLeft = vx
            val cellRight = vx + w

            // Dibuja borde completo del encabezado
            canvas.drawRect(cellLeft, headerTop, cellRight, headerBottom, paintLine)

            // Centra el texto horizontalmente
            val textWidth = paintTableHeader.measureText(c)
            val tx = cellLeft + (w / 2f) - (textWidth / 2f)
            val ty = headerTop + textYOffset

            canvas.drawText(c, tx, ty, paintTableHeader)
            vx += w
        }


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

// === Encabezado sombreado gris: DESCRIPCIÓN DE FALLA ===
        val descHeaderHeight = 16f
        canvas.drawRect(margin, y, pageWidth - margin, y + descHeaderHeight, vehicleTitlePaint)
        val descTitleX = margin + (vehTableWidth / 2f) - (paintBoxTitle.measureText("DESCRIPCIÓN DE FALLA") / 2f)
        canvas.drawText("DESCRIPCIÓN DE FALLA", descTitleX, y + textYOffset, paintBoxTitle)
        y += descHeaderHeight

// === Cuadro de descripción ===
        val descTop = y
        val descHeight = 32f
        canvas.drawRect(margin, descTop, pageWidth - margin, descTop + descHeight, paintLine)
        val falla = orden.descripcionFalla ?: "—"
        drawMultilineText(canvas, falla, margin + 6f, descTop + 10f, pageWidth - margin * 2 - 12f, paintBody)
        y = descTop + descHeight + 20f


        // === CONDICIONES DEL VEHÍCULO (dos columnas) ===
        canvas.drawText("CONDICIONES DEL VEHÍCULO", margin, y, paintBoxTitle)
        y += 6f

        val condiciones = orden.condiciones

        // Definición de itemsLeft e itemsRight
        val itemsLeft = listOf(
            "Espejos",
            "Asientos",
            "Faro delantero",
            "Luz de trasera",
            "Direccionales",
            "Cubiertas",
            "Tapón de gasolina"
        )
        val itemsRight = listOf(
            "Pedales",
            "Parabrisas",
            "Claxon",
            "Tapón de aceite",
            "Tapón radiador",
            "Filtro de aire",
            "Batería",
            "Llaves"
        )

        val condStartY = y
        val condRowHeight = 16f
        val condColGap = 12f
        val condColWidth = (pageWidth - margin * 2 - condColGap) / 2f
        val labels = listOf("PRESENTA", "SI", "NO", "ROTO/DAÑADO")
        val labelWidths = floatArrayOf(condColWidth * 0.45f, condColWidth * 0.16f, condColWidth * 0.16f, condColWidth * 0.23f)
        val condTextYOffset = 12f
        val condHeaderYOffset = 12f

        // Fondo gris para encabezados
        val condHeaderPaint = Paint().apply {
            style = Paint.Style.FILL
            color = Color.LTGRAY
        }

        // Encabezados columna izquierda
        var headerX = margin
        val headerY = condStartY
        for (i in labels.indices) {
            val w = labelWidths[i]
            // Fondo gris
            canvas.drawRect(headerX, headerY, headerX + w, headerY + condRowHeight, condHeaderPaint)
            // Borde
            canvas.drawRect(headerX, headerY, headerX + w, headerY + condRowHeight, paintLine)
            // Texto centrado
            val text = labels[i]
            val textWidth = paintTableHeader.measureText(text)
            val tx = headerX + (w / 2f) - (textWidth / 2f)
            val ty = headerY + condHeaderYOffset
            canvas.drawText(text, tx, ty, paintTableHeader)
            headerX += w
        }
        // Encabezados columna derecha
        headerX = margin + condColWidth + condColGap
        for (i in labels.indices) {
            val w = labelWidths[i]
            canvas.drawRect(headerX, headerY, headerX + w, headerY + condRowHeight, condHeaderPaint)
            canvas.drawRect(headerX, headerY, headerX + w, headerY + condRowHeight, paintLine)
            val text = labels[i]
            val textWidth = paintTableHeader.measureText(text)
            val tx = headerX + (w / 2f) - (textWidth / 2f)
            val ty = headerY + condHeaderYOffset
            canvas.drawText(text, tx, ty, paintTableHeader)
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

// === TEXTO LEGAL Y FIRMAS ===

        val indent = "     " // sangría visual

// 1. Párrafo legal inicial
        val legal1 = "$indent En caso de que el presupuesto no sea aceptado, el consumidor deberá cubrir exclusivamente el costo de la revisión y diagnóstico. El prestador del servicio se compromete a devolver la motocicleta en las mismas condiciones en las que fue entregada, salvo las consecuencias inevitables derivadas del diagnóstico."
        y = drawMultilineText(canvas, legal1, margin, y, pageWidth - margin * 2, paintBody)
        y += 12f

// 2. Costo de revisión, firma del prestador y fecha (alineación vertical + fecha en español)
        val costo = orden.costos.costo
        val costoStr = String.format(Locale("es", "MX"), "%.2f", costo)
        val lineSpacing = 12f

// Costo de la revisión
        canvas.drawText("Costo de la revisión: $$costoStr", margin, y, paintBody)
        y += lineSpacing

// Firma del prestador (texto + línea alineada verticalmente)
        val firmaText = "Firma del prestador de servicios:"
        val firmaY = y
        canvas.drawText(firmaText, margin, firmaY, paintBody)

// Línea a la derecha del texto, alineada con baseline
        val firmaTextWidth = paintBody.measureText(firmaText)
        val lineStartX = margin + firmaTextWidth + 16f
        val lineEndX = lineStartX + 180f
        val lineY = firmaY
        canvas.drawLine(lineStartX, lineY, lineEndX, lineY, paintLine)

        y += lineSpacing

// === Firma optimizada (preprocesada) ===
        if (!firmaBase64.isNullOrBlank()) {
            preprocessSignature(firmaBase64)?.let { sigBitmap ->

                // Escalar para que encaje EXACTO en la línea
                val scaledBitmap = Bitmap.createScaledBitmap(sigBitmap, 180, 80, true)

                // Dibujar justo encima de la línea
                canvas.drawBitmap(
                    scaledBitmap,
                    lineStartX,
                    firmaY - scaledBitmap.height + 8f, // Ajuste para que quede justo arriba
                    null
                )
            }
        }

// Fecha en formato largo español
        val fechaActual = Date()
        val fechaFormateada = SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("es", "MX")).format(fechaActual)
        canvas.drawText("Fecha: $fechaFormateada", margin, y, paintBody)
        y += lineSpacing
        y += 12f



// 3. Cláusula de cesión de vehículo
        val clausula = "$indent El consumidor: ( X ) Acepta que el prestador del servicio pueda ceder o transmitir el vehículo, sus partes o piezas, a terceros (como torneros, soldadores u otros especialistas), ya sea con fines de reparación o para la obtención de cotizaciones de costos y precios. Esto será permitido únicamente en caso de ser estrictamente necesario y siempre que el propietario sea previamente informado de estas acciones y haya dado su consentimiento para el traslado del vehículo o de sus componentes."
        y = drawMultilineText(canvas, clausula, margin, y, pageWidth - margin * 2, paintBody)
        y += 12f

// 4. Cláusula de publicidad con marca dinámica
        val aceptaPublicidad = orden.aceptaEnvioPublicidad
        val publicidad = if (aceptaPublicidad) {
            "( X ) Acepta (   ) No acepta que el prestador de servicios envíe publicidad sobre bienes y servicios."
        } else {
            "(   ) Acepta ( X ) No acepta que el prestador de servicios envíe publicidad sobre bienes y servicios."
        }
        y = drawMultilineText(canvas, publicidad, margin, y, pageWidth - margin * 2, paintBody)
        y += 20f

// === Firma del consumidor (alineada como la del prestador) ===

// Texto: "Firma de autorización del consumidor:"
        val signatureText = "Firma de autorización del consumidor:"
        val sigY = y
        canvas.drawText(signatureText, margin, sigY, paintBody)

// Línea alineada a la derecha del texto
        val sigTextWidth = paintBody.measureText(signatureText)
        val consumerLineStartX = margin + sigTextWidth + 16f
        val consumerLineEndX = consumerLineStartX + 180f
        canvas.drawLine(consumerLineStartX, sigY, consumerLineEndX, sigY, paintLine)

        y += lineSpacing

// Firma digital del consumidor
        orden.firmaClienteBase64?.takeIf { it.isNotBlank() }?.let { b64 ->
            preprocessSignature(b64)?.let { sigBitmap ->

                // Ajustar tamaño para encajar en la línea
                val scaled = Bitmap.createScaledBitmap(sigBitmap, 180, 80, true)

                // Dibujo alineado justo encima de la línea (igual que la primera firma)
                canvas.drawBitmap(
                    scaled,
                    consumerLineStartX,
                    sigY - scaled.height + 8f,
                    null
                )
            }
        }

        y += 32f

// 6. Nota importante en negritas
        val note = "NOTA IMPORTANTE: Al firmar este documento, el consumidor declara haber leído y estar de acuerdo con el reglamento interno del taller mecánico, del cual ha sido informado previamente."
        val boldPaintBody = Paint(paintBody).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        y = drawMultilineText(canvas, note, margin, y, pageWidth - margin * 2, boldPaintBody)


        // Fin de la primera pagnia
        doc.finishPage(page)
// ======== NUEVA PÁGINA PARA REGLAMENTO ========
        val pageInfo2 = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 2).create()
        val page2 = doc.startPage(pageInfo2)
        val canvas2 = page2.canvas

        var y2 = margin

        val titleReglamento = "REGLAMENTO DEL TALLER MECÁNICO DE MOTOS"
        val titleX2 = (pageWidth / 2f) - (paintSectionTitle.measureText(titleReglamento) / 2f)
        canvas2.drawText(titleReglamento, titleX2, y2, paintSectionTitle)
        y2 += 20f

        val subtitle = "~BOX HALACHÓ~"
        val subX2 = (pageWidth / 2f) - (paintHeaderTitle.measureText(subtitle) / 2f)
        canvas2.drawText(subtitle, subX2, y2, paintHeaderTitle)
        y2 += 30f

        val reglamento = """
Para garantizar un servicio eficiente y organizado, así como para evitar inconvenientes tanto para los clientes como para el taller, solicitamos que todos los clientes respeten las siguientes reglas al dejar sus motocicletas para reparación:

**1. RECEPCIÓN DE LA MOTO Y ORDEN DE SERVICIO.**
Toda moto que sea ingresada al taller para su reparación deberá estar acompañada de una Orden de Servicio debidamente llenada. Este documento es esencial para que el taller pueda ofrecer el precio acordado y garantizar que los trabajos realizados sean los correctos. Sin la Orden de Servicio, el precio acordado podría no ser respetado.

**2. PLAZO DE ENTREGA Y CARGOS POR RETRASO.**
Se establecerá un plazo de entrega para cada motocicleta, el cual será acordado entre el cliente y el taller al momento de la recepción. En caso de que el cliente no recoja la moto en la fecha acordada, se cobrará un cargo adicional de **$100 pesos MXN por cada día de retraso**. Este cargo es para cubrir el espacio ocupado en el taller y los costos adicionales generados por la demora.

**3. RESPONSABILIDAD DEL CLIENTE EN LA RETIRADA DE LA MOTO.**
Es responsabilidad del cliente retirar la moto en el plazo acordado. Si el cliente no puede cumplir con la fecha de entrega, deberá notificar al taller con al menos 24 horas de anticipación para acordar una nueva fecha, y se tomará en cuenta si existen cargos adicionales por retrasos previos.

**4. EVALUACIÓN DE DAÑOS Y PRESUPUESTO PREVIO.**
Antes de iniciar cualquier trabajo de reparación, el taller proporcionará un presupuesto detallado que debe ser aprobado por el cliente. El presupuesto podrá modificarse si durante la reparación se encuentran daños adicionales no detectados en la evaluación inicial.

**5. PIEZAS Y REPUESTOS.**
Cualquier repuesto o pieza que se requiera para la reparación de la moto será adquirido por el taller con la aprobación previa del cliente. Los costos de los repuestos serán adicionales al presupuesto inicial y deberán ser cubiertos por el cliente al momento de la entrega de la moto.

**6. GARANTÍA DE LOS TRABAJOS REALIZADOS.**
El taller ofrece una garantía sobre los trabajos realizados, que podrá variar dependiendo del tipo de reparación o servicio. Esta garantía cubre defectos de mano de obra y piezas defectuosas durante un periodo de tiempo determinado.  
**No incluye daños causados por mal uso, accidentes o negligencia del cliente. Además, El Box no se hace responsable por vehículos abandonados por más de 15 días.**

**7. POLÍTICA DE CANCELACIÓN O MODIFICACIÓN DE TRABAJOS.**
Si el cliente decide cancelar o modificar el trabajo una vez iniciado, se cobrará un cargo proporcional al avance realizado, el cual será determinado según el tipo de reparación. La cancelación deberá realizarse por escrito y con al menos 24 horas de antelación.
""".trimIndent()

// --- DIBUJAR TEXTO CON NEGRITAS REALES ---
        y2 = drawRichMultilineText(
            canvas2,
            reglamento,
            margin,
            y2,
            pageWidth - margin * 2,
            paintBody,
            paintBoldBody
        )

        doc.finishPage(page2)

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