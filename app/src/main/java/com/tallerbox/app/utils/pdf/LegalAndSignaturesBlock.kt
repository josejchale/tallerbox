package com.tallerbox.app.utils.pdf

import android.graphics.*
import com.tallerbox.app.model.orden.OrdenConClienteYVehiculo
import com.tallerbox.app.utils.pdf.TextHelpers.drawMultilineText
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object LegalAndSignaturesBlock {

    /**
     * Decodifica Base64 → Bitmap
     */
    private fun decodeBase64(base64: String?): Bitmap? {
        return try {
            if (base64.isNullOrBlank()) return null
            val bytes = Base64.getDecoder().decode(base64)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (_: Exception) { null }
    }

    /**
     * Dibuja la firma justo encima del texto, SIN línea
     */
    private fun drawSignature(canvas: Canvas, bmp: Bitmap, x: Float, y: Float) {
        val targetW = 130f
        val aspect = bmp.height.toFloat() / bmp.width.toFloat()
        val targetH = targetW * aspect
        val dest = RectF(x, y - targetH + 6f, x + targetW, y + 6f)
        canvas.drawBitmap(bmp, null, dest, null)
    }

    fun draw(
        canvas: Canvas,
        data: OrdenConClienteYVehiculo,
        pageWidth: Int,
        startY: Float,
        margin: Float,
        firmaBase64: String?
    ): Float {

        val paintBody = PdfPaints.body

        var y = startY
        val indent = "     "

        val orden = data.orden

        // === 1. PÁRRAFO LEGAL ===
        val legal1 = "${indent}En caso de que el presupuesto no sea aceptado, el consumidor deberá cubrir exclusivamente el costo de la revisión y diagnóstico. El prestador del servicio se compromete a devolver la motocicleta en las mismas condiciones en las que fue entregada, salvo las consecuencias inevitables derivadas del diagnóstico."
        y = drawMultilineText(canvas, legal1, margin, y, pageWidth - margin * 2, paintBody) + 12f

        // === 2. COSTO DE LA REVISIÓN ===
        val costo = orden.costos.costo
        val costoStr = String.format(Locale("es", "MX"), "%.2f", costo)
        val lineSpacing = 12f

        canvas.drawText("Costo de la revisión: $$costoStr", margin, y, paintBody)
        y += lineSpacing

        // === FIRMA DEL PRESTADOR (SIN LÍNEA, SOLO FIRMA) ===
        val prestadorText = "Firma del prestador de servicios:"
        canvas.drawText(prestadorText, margin, y, paintBody)

        val prestadorTextWidth = paintBody.measureText(prestadorText)
        val prestadorSigX = margin + prestadorTextWidth + 20f

        decodeBase64(firmaBase64)?.let { firmaBmp ->
            drawSignature(canvas, firmaBmp, prestadorSigX, y)
        }

        y += lineSpacing

        // === FECHA ===
        val fechaActual = Date()
        val fechaFormateada = SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("es", "MX")).format(fechaActual)
        canvas.drawText("Fecha: $fechaFormateada", margin, y, paintBody)
        y += lineSpacing + 12f

        // === 3. CLÁUSULA CESIÓN ===
        val clausula = "${indent}El consumidor: ( X ) Acepta que el prestador del servicio pueda ceder o transmitir el vehículo, sus partes o piezas, a terceros (como torneros, soldadores u otros especialistas), ya sea con fines de reparación o para la obtención de cotizaciones de costos y precios, siempre bajo consentimiento informado."
        y = drawMultilineText(canvas, clausula, margin, y, pageWidth - margin * 2, paintBody) + 12f

        // === 4. PUBLICIDAD ===
        val aceptaPublicidad = orden.aceptaEnvioPublicidad
        val publicidad = if (aceptaPublicidad)
            "( X ) Acepta   (   ) No acepta que el prestador de servicios envíe publicidad."
        else
            "(   ) Acepta   ( X ) No acepta que el prestador de servicios envíe publicidad."

        y = drawMultilineText(canvas, publicidad, margin, y, pageWidth - margin * 2, paintBody) + 20f

        // === FIRMA DEL CONSUMIDOR ===
        val consumidorText = "Firma de autorización del consumidor:"
        canvas.drawText(consumidorText, margin, y, paintBody)

        val consumidorTextWidth = paintBody.measureText(consumidorText)
        val consumidorSigX = margin + consumidorTextWidth + 20f

        decodeBase64(orden.firmaClienteBase64)?.let { firmaBmp ->
            drawSignature(canvas, firmaBmp, consumidorSigX, y)
        }

        y += lineSpacing + 32f

        // === NOTA IMPORTANTE ===
        val boldPaint = Paint(paintBody).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val note = "NOTA IMPORTANTE: Al firmar este documento, el consumidor declara haber leído y estar de acuerdo con el reglamento interno del taller mecánico."
        y = drawMultilineText(canvas, note, margin, y, pageWidth - margin * 2, boldPaint)

        return y
    }
}
