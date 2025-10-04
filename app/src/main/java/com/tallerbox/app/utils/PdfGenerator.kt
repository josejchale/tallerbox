package com.tallerbox.app.util

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

        // Cliente block
        y += 22f
        canvas.drawText("DATOS DEL CLIENTE", marginLeft, y, paintHeading)
        y += 16f

        val cliente = data.cliente
        val clienteNombre = cliente?.nombreCompleto ?: "—"
        val clienteDireccion = listOfNotNull(
            cliente?.calle,
            cliente?.numeroCasa?.toString(),

            cliente?.municipio,
            cliente?.estado
        ).joinToString(separator = " ").ifBlank { "—" }
        val clienteTel = cliente?.telefono ?: "—"

        canvas.drawText("NOMBRE: $clienteNombre", marginLeft, y, paintNormal)
        y += 14f
        canvas.drawText("DIRECCIÓN: $clienteDireccion", marginLeft, y, paintNormal)
        y += 14f
        canvas.drawText("NÚMERO CELULAR: $clienteTel", marginLeft, y, paintNormal)

        // Orden block
        y += 22f
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

        // Vehicle block
        y += 22f
        canvas.drawText("DATOS DEL VEHÍCULO", marginLeft, y, paintHeading)
        y += 16f

        val veh = data.vehiculo
        val marca = veh?.marca ?: "—"
        val modelo = veh?.modelo ?: "—"
        val ano = veh?.ano ?: "—"
        val colorStr = veh?.color ?: "—"
        val placas = veh?.placa ?: "—"
        val vin = veh?.vin ?: "—"

        // Draw table-like single row
        val colW = (pageWidth - marginLeft * 2) / 6f
        var x = marginLeft
        canvas.drawText("MARCA", x, y, paintSmall); x += colW
        canvas.drawText("MODELO", x, y, paintSmall); x += colW
        canvas.drawText("AÑO", x, y, paintSmall); x += colW
        canvas.drawText("COLOR", x, y, paintSmall); x += colW
        canvas.drawText("PLACAS", x, y, paintSmall); x += colW
        canvas.drawText("VIN", x, y, paintSmall)
        y += 14f
        x = marginLeft
        canvas.drawText(marca, x, y, paintNormal); x += colW
        canvas.drawText(modelo, x, y, paintNormal); x += colW
        canvas.drawText(ano.toString(), x, y, paintNormal); x += colW
        canvas.drawText(colorStr, x, y, paintNormal); x += colW
        canvas.drawText(placas, x, y, paintNormal); x += colW
        canvas.drawText(vin, x, y, paintNormal)
        y += 22f

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
            "Llaves" to condiciones.llaves,
            "Observaciones" to null
        )

        val col1X = marginLeft
        val col2X = pageWidth / 2f
        var condY = y
        listaCondiciones.forEachIndexed { idx, pair ->
            if (pair.first == "Observaciones") {
                val obs = condiciones.observaciones ?: ""
                if (obs.isNotBlank()) {
                    condY = drawMultilineText(canvas, "Observaciones: $obs", marginLeft, condY + 6f, pageWidth - marginLeft * 2, paintNormal)
                    condY += 6f
                }
            } else {
                val left = if (idx % 2 == 0) col1X else col2X
                val estado = pair.second?.name ?: "—"
                canvas.drawText("${pair.first}: $estado", left, condY, paintNormal)
                if (idx % 2 == 1) condY += 14f
            }
        }
        y = condY + 12f

        // Terms and signature area
        val costo = orden.costos.costo
        canvas.drawText("Costo total: $${String.format(Locale("es","MX"), "%.2f", costo)}", marginLeft, y, paintHeading)
        y += 26f

        val docTerms = "En caso de que el presupuesto no sea aceptado, el consumidor deberá cubrir exclusivamente el costo de la revisión y diagnóstico."
        y = drawMultilineText(canvas, docTerms, marginLeft, y, pageWidth - marginLeft * 2, paintSmall)
        y += 26f

        // Signature area: draw a line and place signature bitmap above it if provided
        val signLineY = y + 40f
        canvas.drawLine(marginLeft, signLineY, marginLeft + 220f, signLineY, paintLine)
        canvas.drawText("Firma del prestador de servicios:", marginLeft, signLineY - 10f, paintSmall)

        val consumerLineX = marginLeft + 260f
        val consumerLineY = signLineY
        canvas.drawLine(consumerLineX, consumerLineY, consumerLineX + 220f, consumerLineY, paintLine)
        canvas.drawText("Firma de autorización del consumidor:", consumerLineX, consumerLineY - 10f, paintSmall)

        // Draw signature image if exists (orden.firmaClienteBase64)
        orden.firmaClienteBase64?.takeIf { it.isNotBlank() }?.let { b64 ->
            try {
                val sigBitmap = base64ToBitmap(b64)
                val maxWidth = 200
                val scale = minOf(maxWidth.toFloat() / sigBitmap.width, 1f)
                val sigW = (sigBitmap.width * scale).toInt()
                val sigH = (sigBitmap.height * scale).toInt()
                val destRect = Rect(consumerLineX.toInt(), (consumerLineY - sigH).toInt() - 4, (consumerLineX + sigW).toInt(), consumerLineY.toInt() - 4)
                canvas.drawBitmap(sigBitmap, null, destRect, Paint())
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
        val words = text.split(Regex("\\s+"))
        var x = startX
        var y = startY
        var line = StringBuilder()
        for (w in words) {
            val test = if (line.isEmpty()) w else "${line} $w"
            val width = paint.measureText(test)
            if (width > maxWidth) {
                canvas.drawText(line.toString(), x, y, paint)
                line = StringBuilder(w)
                y += paint.textSize + 4f
            } else {
                if (line.isNotEmpty()) line.append(" ")
                line.append(w)
            }
        }
        if (line.isNotEmpty()) {
            canvas.drawText(line.toString(), x, y, paint)
            y += paint.textSize + 4f
        }
        return y
    }

    private fun base64ToBitmap(base64: String): Bitmap {
        val decoded = Base64.decode(base64, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
    }

    /**
     * --- Helpers para compartir/abrir ---
     *
     * generateAndGetUri: genera el PDF en cacheDir con el nombre indicado y devuelve el Uri firmado por FileProvider.
     * openPdf: abre el Pdf en un viewer instalado.
     * sharePdf: lanza un intent chooser para compartir el PDF.
     *
     * Nota: Declara en AndroidManifest.xml el FileProvider con authority "${applicationId}.fileprovider"
     * y crea res/xml/file_paths.xml con paths apuntando a cache-path o external-path según tu preferencia.
     */
    fun generateAndGetUri(context: Context, data: OrdenConClienteYVehiculo, filename: String = "orden.pdf", providerAuthority: String): Uri {
        // generar archivo en cacheDir para compartir fácilmente
        val outFile = File(context.cacheDir, filename)
        if (outFile.exists()) outFile.delete()
        generateOrdenPdf(context, data, outFile)
        return FileProvider.getUriForFile(context, providerAuthority, outFile)
    }

    fun openPdf(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun sharePdf(context: Context, uri: Uri, chooserTitle: String = "Compartir PDF") {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val chooser = Intent.createChooser(shareIntent, chooserTitle)
        context.startActivity(chooser)
    }
}
