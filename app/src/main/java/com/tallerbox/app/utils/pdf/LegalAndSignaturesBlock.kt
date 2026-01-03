package com.tallerbox.app.utils.pdf

import android.graphics.*
import android.util.Base64
import com.tallerbox.app.model.orden.OrdenConClienteYVehiculo
import com.tallerbox.app.utils.pdf.TextHelpers.drawMultilineText
import java.text.SimpleDateFormat
import java.util.*
import com.tallerbox.app.utils.pdf.TextHelpers.drawRichMultilineText

object LegalAndSignaturesBlock {

    /**
     * Decodifica Base64 → Bitmap
     */
    private fun decodeBase64(base64: String?): Bitmap? {
        return try {
            if (base64.isNullOrBlank()) return null

            val cleanBase64 = base64
                .replace("data:image/png;base64,", "")
                .replace("data:image/jpeg;base64,", "")
                .trim()

            val bytes = Base64.decode(cleanBase64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null
        }
    }

    private fun drawSignature(
        canvas: Canvas,
        bmp: Bitmap,
        x: Float,
        baselineY: Float,
        textPaint: Paint
    ) {
        // 🔧 Tamaño de firma
        val scale = 2f
        val baseWidth = 200f
        val targetW = baseWidth * scale

        val aspect = bmp.height.toFloat() / bmp.width.toFloat()
        val targetH = targetW * aspect

        // ➖ Línea más corta (70% del ancho de la firma)
        val lineWidth = targetW * 0.7f
        val lineStartX = x
        val lineEndX = x + lineWidth

        // 📍 Posición base de la línea (no altera el flujo)
        val lineY = baselineY + 6f

        // ✍️ Firma centrada y justo encima de la línea
        val gap = -20f // separación mínima firma-línea
        val signatureBottom = lineY - gap
        val signatureTop = signatureBottom - targetH

        val dest = RectF(
            x,
            signatureTop,
            x + targetW,
            signatureBottom
        )

        // Dibujar firma
        canvas.drawBitmap(bmp, null, dest, null)

        // Dibujar línea
        val linePaint = Paint(textPaint).apply {
            strokeWidth = 1.5f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        canvas.drawLine(
            lineStartX,
            lineY,
            lineEndX,
            lineY,
            linePaint
        )
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
        val lineSpacing = 10f

        // === PÁRRAFO LEGAL ===
        val legal1 =
            "${indent}En caso de que el presupuesto no sea aceptado, el consumidor deberá cubrir exclusivamente el costo de la revisión y diagnóstico. El prestador del servicio se compromete a devolver la motocicleta en las mismas condiciones en las que fue entregada, salvo las consecuencias inevitables derivadas del diagnóstico."

        y = drawMultilineText(
            canvas,
            legal1,
            margin,
            y,
            pageWidth - margin * 2,
            paintBody
        ) + 12f

        // === COSTO ===
        val costoStr = String.format(Locale("es", "MX"), "%.2f", orden.costos.costo)
        canvas.drawText("Costo de la revisión: $$costoStr", margin, y, paintBody)
        y += lineSpacing

        // === FIRMA PRESTADOR ===
        val prestadorText = "Firma del prestador de servicios:"
        canvas.drawText(prestadorText, margin, y, paintBody)

        val prestadorSigX =
            margin + paintBody.measureText(prestadorText) + 10f

        decodeBase64(firmaBase64)?.let { bmp ->
            drawSignature(canvas, bmp, prestadorSigX, y, paintBody)
        }

        y += lineSpacing

        // === FECHA ===
        val fecha = SimpleDateFormat(
            "d 'de' MMMM 'de' yyyy",
            Locale("es", "MX")
        ).format(Date())

        canvas.drawText("Fecha: $fecha", margin, y, paintBody)
        y += lineSpacing + 10f

        // === CLÁUSULA ===
        val clausula =
            "${indent}El consumidor: ( X ) Acepta que el prestador del servicio pueda ceder o transmitir el vehículo, sus partes o piezas, a terceros (como torneros, soldadores u otros especialistas), ya sea con fines de reparación o para la obtención de cotizaciones de costos y precios, siempre bajo consentimiento informado."

        y = drawMultilineText(
            canvas,
            clausula,
            margin,
            y,
            pageWidth - margin * 2,
            paintBody
        ) + 12f

        // === PUBLICIDAD ===
        val publicidad =
            if (orden.aceptaEnvioPublicidad)
                "( X ) Acepta   (   ) No acepta que el prestador de servicios envíe publicidad."
            else
                "(   ) Acepta   ( X ) No acepta que el prestador de servicios envíe publicidad."

        y = drawMultilineText(
            canvas,
            publicidad,
            margin,
            y,
            pageWidth - margin * 2,
            paintBody
        ) + 20f

        // === FIRMA CONSUMIDOR ===
        val consumidorText = "Firma de autorización del consumidor:"
        canvas.drawText(consumidorText, margin, y, paintBody)

        val consumidorSigX =
            margin + paintBody.measureText(consumidorText) + 10f

        decodeBase64(orden.firmaClienteBase64)?.let { bmp ->
            drawSignature(canvas, bmp, consumidorSigX, y, paintBody)
        }

        y += lineSpacing + 10f

        // === NOTA FINAL ===
        val boldPaint = Paint(paintBody).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val note =
            "NOTA IMPORTANTE: Al firmar este documento, el consumidor declara haber leído y estar de acuerdo con el reglamento interno del taller mecánico."

        y = drawMultilineText(
            canvas,
            note,
            margin,
            y,
            pageWidth - margin * 2,
            boldPaint
        )

        return y
    }
}
