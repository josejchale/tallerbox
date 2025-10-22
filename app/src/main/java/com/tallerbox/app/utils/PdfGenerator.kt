package com.tallerbox.app.utils

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.tallerbox.app.model.orden.OrdenConClienteYVehiculo
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import android.util.Base64
import android.graphics.BitmapFactory

object PdfGenerator {

    private val localeMx = Locale.Builder().setLanguage("es").setRegion("MX").build()
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", localeMx)

    @Throws(Exception::class)
    fun generateOrdenPdf(context: Context, data: OrdenConClienteYVehiculo, outFile: File): File {
        val pageWidth = 595
        val pageHeight = 842
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
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.BLACK
            isAntiAlias = true
        }
        val paintText = Paint().apply {
            textSize = 10.5f
            color = Color.BLACK
            isAntiAlias = true
        }
        val paintSmall = Paint().apply {
            textSize = 9f
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
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        var y = margin

        // === ENCABEZADO ===
        canvas.drawText("TALLER DE MOTOS", margin, y, paintHeading)
        y += 14f
        canvas.drawText("~BOX HALACHÓ~", margin, y, paintHeading)
        y += 14f
        canvas.drawText("TELÉFONO: 999-333-68-77", margin, y, paintText)
        y += 12f
        canvas.drawText("DIRECCIÓN: CALLE 21 #30K ENTRE 10 Y 12 COL. SAN FRANCISCO", margin, y, paintText)
        y += 24f

        canvas.drawText("ORDEN DE SERVICIO", margin, y, paintTitle)
        y += 8f
        canvas.drawLine(margin, y, pageWidth - margin, y, paintLine)
        y += 16f

        // === DATOS DEL CLIENTE Y ORDEN ===
        val colWidth = (pageWidth - margin * 2) / 2f
        val topCliente = y

        canvas.drawRect(margin, topCliente, margin + colWidth, topCliente + 90, paintLine)
        canvas.drawRect(margin + colWidth, topCliente, pageWidth - margin, topCliente + 90, paintLine)

        // Subtítulos
        canvas.drawText("DATOS DEL CLIENTE", margin + 6, topCliente + 12, paintHeading)
        canvas.drawText("DATOS DE ORDEN DE SERVICIO", margin + colWidth + 6, topCliente + 12, paintHeading)

        val cliente = data.cliente
        val orden = data.orden

        // CLIENTE
        var yCliente = topCliente + 26
        val clienteNombre = cliente?.nombreCompleto ?: "—"
        val clienteDireccion = buildString {
            append(cliente?.calle ?: "")
            cliente?.calle1?.let { if (it.isNotBlank()) append(" e/ $it") }
            cliente?.calle2?.let { if (it.isNotBlank()) append(" y $it") }
            cliente?.numeroCasa?.let { if (it.isNotBlank()) append(" #$it") }
            cliente?.municipio?.let { if (it.isNotBlank()) append(", $it") }
            cliente?.estado?.let { if (it.isNotBlank()) append(", $it") }
        }.ifBlank { "—" }
        val clienteTel = cliente?.telefono ?: "—"

        canvas.drawText("NOMBRE: $clienteNombre", margin + 6, yCliente, paintText); yCliente += 14f
        canvas.drawText("DIRECCIÓN: $clienteDireccion", margin + 6, yCliente, paintText); yCliente += 14f
        canvas.drawText("CELULAR: $clienteTel", margin + 6, yCliente, paintText)

        // ORDEN
        val numeroOrden = orden.numeroOrden ?: "—"
        val fechaIngreso = dateFormatter.format(orden.fechaIngreso)
        val fechaEntrega = orden.fechaEntregaEstimado?.let { dateFormatter.format(it) } ?: "—"

        var yOrden = topCliente + 26
        canvas.drawText("N° ORDEN: $numeroOrden", margin + colWidth + 6, yOrden, paintText); yOrden += 14f
        canvas.drawText("INGRESO: $fechaIngreso", margin + colWidth + 6, yOrden, paintText); yOrden += 14f
        canvas.drawText("ENTREGA: $fechaEntrega", margin + colWidth + 6, yOrden, paintText)

        y = topCliente + 100f

        // === DATOS VEHÍCULO ===
        canvas.drawText("DATOS DEL VEHÍCULO", margin, y, paintHeading)
        y += 6f
        canvas.drawLine(margin, y, pageWidth - margin, y, paintLine)
        y += 14f

        val veh = data.vehiculo
        val vehData = listOf(
            "MARCA" to (veh?.marca ?: "—"),
            "MODELO" to (veh?.modelo ?: "—"),
            "AÑO" to (veh?.ano ?: "—"),
            "COLOR" to (veh?.color ?: "—"),
            "PLACAS" to (veh?.placa ?: "—"),
            "VIN" to (veh?.vin ?: "—")
        )

        val cellWidth = (pageWidth - margin * 2) / vehData.size
        var x = margin
        vehData.forEach { (title, _) ->
            canvas.drawText(title, x + 4, y, paintHeading)
            x += cellWidth
        }
        y += 14f
        x = margin
        vehData.forEach { (_, value) ->
            canvas.drawText(value, x + 4, y, paintText)
            x += cellWidth
        }

        y += 24f
        canvas.drawText("DESCRIPCIÓN DE FALLA", margin, y, paintHeading)
        y += 14f
        val falla = orden.descripcionFalla ?: "—"
        y = drawMultilineText(canvas, falla, margin + 6, y, pageWidth - margin * 2, paintText)

        y += 14f
        canvas.drawText("CONDICIONES DEL VEHÍCULO", margin, y, paintHeading)
        y += 10f

        // === CONDICIONES EN 2 COLUMNAS ===
        val condiciones = orden.condiciones
        val tabla = listOf(
            "Espejos", "Asientos", "Faro delantero", "Luz de paro", "Trasero",
            "Direccionales", "Cubiertas", "Compuertas", "Tapón gasolina",
            "Pedales", "Parabrisas", "Claxon", "Tapón aceite", "Tapón radiador",
            "Filtro aire", "Batería", "Llaves"
        )

        val colAncho = (pageWidth - margin * 2) / 2f
        val filaAltura = 16f
        val colLabels = listOf("PRESENTA", "SI", "NO", "DAÑADO")
        // anchos de columnas (ajustables)
        val colWidths = floatArrayOf(90f, 25f, 25f, 50f)

        var startY = y
        var startX = margin

        // recorremos por índices para dividir en dos columnas
        for (i in tabla.indices) {
            // cambiar a segunda columna cuando lleguemos a la mitad
            if (i == tabla.size / 2) {
                startX = margin + colAncho
                startY = y
            }

            var xPos = startX
            // dibujar celda de la fila
            colLabels.forEachIndexed { c, _ ->
                val rectLeft = xPos
                val rectRight = xPos + colWidths[c]
                val rectTop = startY
                val rectBottom = startY + filaAltura
                canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, paintLine)
                if (c == 0) {
                    // texto del ítem
                    canvas.drawText(tabla[i], rectLeft + 4, rectTop + 12, paintText)
                }
                xPos += colWidths[c]
            }

            // === MARCAR CON X SEGÚN CONDICIÓN (CORRECCIÓN: no usar .get) ===
            val condText = getCondValue(condiciones, tabla[i])?.lowercase(Locale.ROOT)
            if (!condText.isNullOrBlank()) {
                val xCol = when {
                    condText.contains("si") -> 1
                    condText.contains("no") -> 2
                    condText.contains("dañ") || condText.contains("roto") -> 3
                    else -> null
                }
                if (xCol != null) {
                    // calcular posicion para la X: sumamos anchos de las columnas anteriores y centramos un poco
                    val xMark = startX + colWidths.take(xCol).sum() + 8f
                    val yMark = startY + filaAltura - 4f
                    canvas.drawText("X", xMark, yMark, paintX)
                }
            }

            startY += filaAltura
        }

        y = maxOf(startY, y) + 20f

        // === TEXTO LEGAL ===
        val costo = orden.costos.costo
        canvas.drawText("Costo de revisión: $${String.format(Locale("es","MX"), "%.2f", costo)}", margin, y, paintHeading)
        y += 20f

        val parrafo1 = "En caso de no aceptar el presupuesto, el consumidor cubrirá solo el costo de la revisión. El taller devolverá la motocicleta en las mismas condiciones en que fue entregada."
        y = drawMultilineText(canvas, parrafo1, margin, y, pageWidth - margin * 2, paintSmall)
        y += 20f

        val clausula = "El consumidor: ( ) Acepta que el taller pueda enviar el vehículo o partes a terceros (torneros, soldadores, etc.) para su reparación. ( ) No acepta el uso de publicidad."
        y = drawMultilineText(canvas, clausula, margin, y, pageWidth - margin * 2, paintSmall)

        // === FIRMAS ===
        y += 40f
        val signLineY = y
        canvas.drawLine(margin, signLineY, margin + 220f, signLineY, paintLine)
        canvas.drawText("Firma del prestador de servicios", margin, signLineY + 12f, paintSmall)

        val consumerLineX = margin + 260f
        canvas.drawLine(consumerLineX, signLineY, consumerLineX + 220f, signLineY, paintLine)
        canvas.drawText("Firma del consumidor", consumerLineX, signLineY + 12f, paintSmall)

        // Firma del cliente (si existe)
        orden.firmaClienteBase64?.takeIf { it.isNotBlank() }?.let { b64 ->
            try {
                val sigBitmap = base64ToBitmap(b64)
                val maxWidth = 180f
                val scale = minOf(maxWidth / sigBitmap.width, 1f)
                val sigW = (sigBitmap.width * scale).toInt()
                val sigH = (sigBitmap.height * scale).toInt()
                val destRect = Rect(
                    consumerLineX.toInt(),
                    (signLineY - sigH).toInt(),
                    (consumerLineX + sigW).toInt(),
                    signLineY.toInt()
                )
                canvas.drawBitmap(sigBitmap, null, destRect, null)
            } catch (_: Exception) {}
        }

        y += 60f
        val nota = "NOTA: Al firmar este documento, el consumidor declara haber leído y aceptado las condiciones del servicio del taller."
        drawMultilineText(canvas, nota, margin, y, pageWidth - margin * 2, paintSmall)

        doc.finishPage(page)
        FileOutputStream(outFile).use { out -> doc.writeTo(out) }
        doc.close()
        return outFile
    }

    /**
     * Devuelve el valor textual (ej. "si", "no", "dañado") para la condición llamada `key`.
     * Ajusta los nombres según las propiedades reales de tu modelo `condiciones`.
     */
    private fun getCondValue(condiciones: Any?, key: String): String? {
        // Si tu modelo tiene un tipo concreto cambia Any? por ese tipo (ej. Condiciones?).
        // Aquí hacemos un mapeo manual: adapta los nombres de propiedad a los de tu modelo real.
        try {
            // Si conoces la clase concreta, cámbiala aquí y evita reflection.
            val clazz = condiciones?.javaClass ?: return null
            return when (key) {
                "Espejos" -> clazz.getDeclaredField("espejos").let {
                    it.isAccessible = true
                    (it.get(condiciones) as? Enum<*>)?.name
                }
                "Asientos" -> clazz.getDeclaredField("asientos").let {
                    it.isAccessible = true
                    (it.get(condiciones) as? Enum<*>)?.name
                }
                "Faro delantero" -> clazz.getDeclaredField("faroDelantero").let {
                    it.isAccessible = true
                    (it.get(condiciones) as? Enum<*>)?.name
                }
                "Luz de paro" -> clazz.getDeclaredField("luzTrasera").let {
                    it.isAccessible = true
                    (it.get(condiciones) as? Enum<*>)?.name
                }
                "Trasero" -> clazz.getDeclaredField("luzTrasera").let {
                    it.isAccessible = true
                    (it.get(condiciones) as? Enum<*>)?.name
                }
                "Direccionales" -> clazz.getDeclaredField("direccionales").let {
                    it.isAccessible = true
                    (it.get(condiciones) as? Enum<*>)?.name
                }
                "Cubiertas" -> clazz.getDeclaredField("cubiertas").let {
                    it.isAccessible = true
                    (it.get(condiciones) as? Enum<*>)?.name
                }
                "Compuertas" -> clazz.getDeclaredField("taponGasolina").let {
                    it.isAccessible = true
                    (it.get(condiciones) as? Enum<*>)?.name
                }
                "Tapón gasolina" -> clazz.getDeclaredField("taponGasolina").let {
                    it.isAccessible = true
                    (it.get(condiciones) as? Enum<*>)?.name
                }
                "Pedales" -> clazz.getDeclaredField("pedales").let {
                    it.isAccessible = true
                    (it.get(condiciones) as? Enum<*>)?.name
                }
                "Parabrisas" -> clazz.getDeclaredField("parabrisas").let {
                    it.isAccessible = true
                    (it.get(condiciones) as? Enum<*>)?.name
                }
                "Claxon" -> clazz.getDeclaredField("claxon").let {
                    it.isAccessible = true
                    (it.get(condiciones) as? Enum<*>)?.name
                }
                "Tapón aceite" -> clazz.getDeclaredField("taponAceite").let {
                    it.isAccessible = true
                    (it.get(condiciones) as? Enum<*>)?.name
                }
                "Tapón radiador", "Tapón radiadores", "Tapón radiador" -> clazz.getDeclaredField("taponRadiador").let {
                    it.isAccessible = true
                    (it.get(condiciones) as? Enum<*>)?.name
                }
                "Tapón radiador" -> clazz.getDeclaredField("taponRadiador").let {
                    it.isAccessible = true
                    (it.get(condiciones) as? Enum<*>)?.name
                }
                "Tapón radiadores" -> clazz.getDeclaredField("taponRadiador").let {
                    it.isAccessible = true
                    (it.get(condiciones) as? Enum<*>)?.name
                }
                "Filtro aire", "Filtro de aire" -> clazz.getDeclaredField("filtroAire").let {
                    it.isAccessible = true
                    (it.get(condiciones) as? Enum<*>)?.name
                }
                "Filtro de aire" -> clazz.getDeclaredField("filtroAire").let {
                    it.isAccessible = true
                    (it.get(condiciones) as? Enum<*>)?.name
                }
                "Batería" -> clazz.getDeclaredField("bateria").let {
                    it.isAccessible = true
                    (it.get(condiciones) as? Enum<*>)?.name
                }
                "Llaves" -> clazz.getDeclaredField("llaves").let {
                    it.isAccessible = true
                    (it.get(condiciones) as? Enum<*>)?.name
                }
                else -> null
            }
        } catch (ex: Exception) {
            // Si reflection falla, devolvemos null.
            return null
        }
    }

    private fun drawMultilineText(canvas: Canvas, text: String, startX: Float, startY: Float, maxWidth: Float, paint: Paint): Float {
        var currentY = startY
        val lines = text.split("\n")
        for (line in lines) {
            val words = line.split(Regex("\\s+"))
            var currentLine = ""
            for (word in words) {
                val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                if (paint.measureText(testLine) > maxWidth) {
                    canvas.drawText(currentLine, startX, currentY, paint)
                    currentLine = word
                    currentY += paint.fontSpacing
                } else {
                    currentLine = testLine
                }
            }
            if (currentLine.isNotEmpty()) {
                canvas.drawText(currentLine, startX, currentY, paint)
                currentY += paint.fontSpacing
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
