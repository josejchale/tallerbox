package com.tallerbox.app.utils

import android.content.Context
import android.content.Intent
import android.graphics.*
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

    // Locale y formateador
    private val localeMx = Locale.Builder().setLanguage("es").setRegion("MX").build()
    private val dateFormatter = SimpleDateFormat("dd MMM yyyy", localeMx)

    /**
     * Dibuja una línea separadora de sección.
     */
    private fun drawSectionSeparator(canvas: Canvas, y: Float, pageWidth: Int, paint: Paint) {
        val margin = 36f
        canvas.drawLine(margin, y, pageWidth - margin, y, paint)
    }

    /**
     * Genera el PDF en el File especificado y devuelve el File resultante.
     * Lanza excepción si algo falla.
     */
    @Throws(Exception::class)
    fun generateOrdenPdf(context: Context, data: OrdenConClienteYVehiculo, outFile: File): File {
        val pageWidth = 595 // A4 points ~ 595 x 842
        val pageHeight = 842

        val doc = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = doc.startPage(pageInfo)
        val canvas = page.canvas

        // Paints
        val paintTitle = Paint().apply {
            isAntiAlias = true
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.BLACK
        }
        val paintHeading = Paint().apply {
            isAntiAlias = true
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.BLACK
        }
        val paintNormal = Paint().apply {
            isAntiAlias = true
            textSize = 11f
            color = Color.BLACK
        }
        val paintSmall = Paint().apply {
            isAntiAlias = true
            textSize = 9f
            color = Color.DKGRAY
        }
        val paintLine = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
        }
        val separatorPaint = Paint().apply {
            color = Color.BLACK // Color negro para los márgenes
            strokeWidth = 1.5f
        }

        val marginLeft = 36f
        var y = 36f

        // Header: Taller info
        canvas.drawText("TALLER DE MOTOS", marginLeft, y, paintSmall)
        y += 14f
        canvas.drawText("~BOX HALACHÓ~", marginLeft, y, paintSmall)
        y += 14f
        canvas.drawText("TELÉFONO: 999-333-68-77", marginLeft, y, paintSmall)
        y += 12f
        canvas.drawText("DIRECCIÓN: CALLE 21 #30K ENTRE 10 Y 12 COLONIA SAN FRANCISCO", marginLeft, y, paintSmall)

        // Title
        y += 28f
        canvas.drawText("ORDEN DE SERVICIO", marginLeft, y, paintTitle)

        // Separator
        y += 12f
        drawSectionSeparator(canvas, y, pageWidth, separatorPaint)
        y += 18f

        // Cliente block
        canvas.drawText("DATOS DEL CLIENTE", marginLeft, y, paintHeading)
        y += 16f

        val cliente = data.cliente
        val clienteNombre = cliente?.nombreCompleto ?: "—"
        val clienteDireccion = buildString {
            append(cliente?.calle ?: "")
            cliente?.calle1?.let { if (it.isNotBlank()) append(" e/ $it") }
            cliente?.calle2?.let { if (it.isNotBlank()) append(" y $it") }
            cliente?.numeroCasa?.let { append(" #$it") }
            cliente?.municipio?.let { if (it.isNotBlank()) append(", $it") }
            cliente?.estado?.let { if (it.isNotBlank()) append(", $it") }
        }.ifBlank { "—" }
        val clienteTel = cliente?.telefono ?: "—"

        canvas.drawText("NOMBRE: $clienteNombre", marginLeft, y, paintNormal)
        y += 14f
        canvas.drawText("DIRECCIÓN: $clienteDireccion", marginLeft, y, paintNormal)
        y += 14f
        canvas.drawText("NÚMERO CELULAR: $clienteTel", marginLeft, y, paintNormal)

        // Separator
        y += 12f
        drawSectionSeparator(canvas, y, pageWidth, separatorPaint)
        y += 18f

        // Orden block
        canvas.drawText("DATOS DE ORDEN DE SERVICIO", marginLeft, y, paintHeading)
        y += 16f

        val orden = data.orden
        val numeroOrden = orden.numeroOrden ?: "—"
        val fechaIngreso = dateFormatter.format(orden.fechaIngreso)
        val fechaEntregaEst = orden.fechaEntregaEstimado?.let { dateFormatter.format(it) } ?: "—"
        canvas.drawText("NÚMERO DE ORDEN: $numeroOrden", marginLeft, y, paintNormal)
        y += 14f
        canvas.drawText("FECHA DE INGRESO: $fechaIngreso", marginLeft, y, paintNormal)
        y += 14f
        canvas.drawText("FECHA DE ENTREGA: $fechaEntregaEst", marginLeft, y, paintNormal)

        // Separator
        y += 12f
        drawSectionSeparator(canvas, y, pageWidth, separatorPaint)
        y += 18f

        // Vehicle block
        canvas.drawText("DATOS DEL VEHÍCULO", marginLeft, y, paintHeading)
        y += 16f

        val veh = data.vehiculo
        val marca = veh?.marca ?: "—"
        val modelo = veh?.modelo ?: "—"
        val ano = veh?.ano ?: "—"
        val colorStr = veh?.color ?: "—"
        val placas = veh?.placa ?: "—"
        val vin = veh?.vin ?: "—"

        val colW = (pageWidth - marginLeft * 2) / 6f
        var x = marginLeft
        // Headers en negrita
        canvas.drawText("MARCA", x, y, paintHeading); x += colW
        canvas.drawText("MODELO", x, y, paintHeading); x += colW
        canvas.drawText("AÑO", x, y, paintHeading); x += colW
        canvas.drawText("COLOR", x, y, paintHeading); x += colW
        canvas.drawText("PLACAS", x, y, paintHeading); x += colW
        canvas.drawText("VIN", x, y, paintHeading)
        y += 14f
        x = marginLeft
        // Valores en texto normal
        canvas.drawText(marca, x, y, paintNormal); x += colW
        canvas.drawText(modelo, x, y, paintNormal); x += colW
        canvas.drawText(ano.toString(), x, y, paintNormal); x += colW
        canvas.drawText(colorStr, x, y, paintNormal); x += colW
        canvas.drawText(placas, x, y, paintNormal); x += colW
        canvas.drawText(vin, x, y, paintNormal)

        // Separator
        y += 18f
        drawSectionSeparator(canvas, y, pageWidth, separatorPaint)
        y += 18f

        // Description
        canvas.drawText("DESCRIPCIÓN DE FALLA", marginLeft, y, paintHeading)
        y += 14f
        val falla = orden.descripcionFalla ?: "—"
        y = drawMultilineText(canvas, falla, marginLeft, y, pageWidth - marginLeft * 2, paintNormal)

        y += 8f
        canvas.drawText("CONDICIONES DEL VEHÍCULO", marginLeft, y, paintHeading)
        y += 14f

        // Conditions: draw two columns
        val condiciones = orden.condiciones
        val listaCondiciones = listOf(
            "Espejos" to condiciones.espejos,
            "Asientos" to condiciones.asientos,
            "Faro delantero" to condiciones.faroDelantero,
            "Luz trasera" to condiciones.luzTrasera,
            "Direccionales" to condiciones.direccionales,
            "Cubiertas" to condiciones.cubiertas,
            "Tapón gasolina" to condiciones.taponGasolina,
            "Pedales" to condiciones.pedales,
            "Parabrisas" to condiciones.parabrisas,
            "Claxon" to condiciones.claxon,
            "Tapón aceite" to condiciones.taponAceite,
            "Tapón radiador" to condiciones.taponRadiador,
            "Filtro aire" to condiciones.filtroAire,
            "Batería" to condiciones.bateria,
            "Llaves" to condiciones.llaves
        )

        val col1X = marginLeft
        val col2X = pageWidth / 2f
        var condY = y
        val itemsPerCol = (listaCondiciones.size + 1) / 2

        for(i in 0 until itemsPerCol) {
            // Columna 1
            val item1 = listaCondiciones[i]
            val estado1 = item1.second?.name ?: "—"
            canvas.drawText("${item1.first}: $estado1", col1X, condY, paintNormal)

            // Columna 2
            val index2 = i + itemsPerCol
            if (index2 < listaCondiciones.size) {
                val item2 = listaCondiciones[index2]
                val estado2 = item2.second?.name ?: "—"
                canvas.drawText("${item2.first}: $estado2", col2X, condY, paintNormal)
            }
            condY += 14f
        }
        y = condY

        // Observaciones
        condiciones.observaciones?.takeIf { it.isNotBlank() }?.let {
            y += 6f
            canvas.drawText("Observaciones:", marginLeft, y, paintHeading)
            y += 14f
            y = drawMultilineText(canvas, it, marginLeft, y, pageWidth - marginLeft * 2, paintNormal)
        }

        // Separator
        y += 12f
        drawSectionSeparator(canvas, y, pageWidth, separatorPaint)
        y += 18f

        // Terms and signature area
        val costo = orden.costos.costo
        canvas.drawText("Costo total: $${String.format(Locale("es","MX"), "%.2f", costo)}", marginLeft, y, paintHeading)
        y += 26f

        val docTerms = "En caso de que el presupuesto no sea aceptado, el consumidor deberá cubrir exclusivamente el costo de la revisión y diagnóstico."
        y = drawMultilineText(canvas, docTerms, marginLeft, y, pageWidth - marginLeft * 2, paintSmall)
        y += 26f

        // Signature area
        val signLineY = y + 40f
        canvas.drawLine(marginLeft, signLineY, marginLeft + 220f, signLineY, paintLine)
        canvas.drawText("Firma del prestador de servicios:", marginLeft, signLineY - 10f, paintSmall)

        val consumerLineX = marginLeft + 260f
        canvas.drawLine(consumerLineX, signLineY, consumerLineX + 220f, signLineY, paintLine)
        canvas.drawText("Firma de autorización del consumidor:", consumerLineX, signLineY - 10f, paintSmall)

        // Draw signature image
        orden.firmaClienteBase64?.takeIf { it.isNotBlank() }?.let { b64 ->
            try {
                val sigBitmap = base64ToBitmap(b64)
                val maxWidth = 200f
                val scale = minOf(maxWidth / sigBitmap.width, 1f)
                val sigW = (sigBitmap.width * scale).toInt()
                val sigH = (sigBitmap.height * scale).toInt()
                val destRect = Rect(consumerLineX.toInt(), (signLineY - sigH).toInt() - 4, (consumerLineX + sigW).toInt(), signLineY.toInt() - 4)
                canvas.drawBitmap(sigBitmap, null, destRect, null)
            } catch (ex: Exception) {
                // ignore drawing signature if decode fails
            }
        }

        // Footer: reglamento breve
        y = signLineY + 60f
        val footerTitle = "REGLAMENTO DEL TALLER MECÁNICO DE MOTOS ~BOX HALACHÓ~"
        canvas.drawText(footerTitle, marginLeft, y, paintHeading)
        y += 14f
        val footerText = "Al firmar este documento, el consumidor declara haber leído y estar de acuerdo con el reglamento interno del taller mecánico."
        drawMultilineText(canvas, footerText, marginLeft, y, pageWidth - marginLeft * 2, paintSmall)

        doc.finishPage(page)

        // Write to file
        FileOutputStream(outFile).use { out ->
            doc.writeTo(out)
        }
        doc.close()
        return outFile
    }

    private fun drawMultilineText(canvas: Canvas, text: String, startX: Float, startY: Float, maxWidth: Float, paint: Paint): Float {
        var currentY = startY
        text.split('\n').forEach { line ->
            val words = line.split(Regex("\\s+"))
            val lineBuffer = mutableListOf<String>()

            for (word in words) {
                val testLine = (lineBuffer + word).joinToString(" ")
                if (paint.measureText(testLine) > maxWidth && lineBuffer.isNotEmpty()) {
                    canvas.drawText(lineBuffer.joinToString(" "), startX, currentY, paint)
                    currentY += paint.fontSpacing
                    lineBuffer.clear()
                }
                lineBuffer.add(word)
            }

            if (lineBuffer.isNotEmpty()) {
                canvas.drawText(lineBuffer.joinToString(" "), startX, currentY, paint)
                currentY += paint.fontSpacing
            }
        }
        return currentY
    }

    private fun base64ToBitmap(base64: String): Bitmap {
        val decoded = Base64.decode(base64, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
    }

    /**
     * --- Helpers para compartir/abrir ---
     */
    fun generateAndGetUri(context: Context, data: OrdenConClienteYVehiculo, filename: String = "orden.pdf", providerAuthority: String): Uri {
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
