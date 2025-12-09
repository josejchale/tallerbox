package com.tallerbox.app.helper

import android.graphics.*
import android.util.Base64

data class FirmaDecoded(val ratio: Float, val base64: String)

/**
 * Parsea la cadena de firma con prefijo "R$ratio$base64"
 */
fun parseFirma(firma: String?): FirmaDecoded? {
    if (firma.isNullOrBlank()) return null
    return if (firma.startsWith("R$")) {
        val parts = firma.split("$")
        val ratio = parts.getOrNull(1)?.toFloatOrNull() ?: 0.8f
        val base64 = parts.drop(2).joinToString("$")
        FirmaDecoded(ratio, base64)
    } else {
        FirmaDecoded(0.8f, firma)
    }
}

/**
 * Convierte fondo blanco en transparente
 */
private fun removeWhiteBackground(bitmap: Bitmap): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val result = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    val pixels = IntArray(width * height)
    result.getPixels(pixels, 0, width, 0, 0, width, height)

    val threshold = 240
    for (i in pixels.indices) {
        val color = pixels[i]
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        if (r > threshold && g > threshold && b > threshold) {
            pixels[i] = Color.TRANSPARENT
        }
    }
    result.setPixels(pixels, 0, width, 0, 0, width, height)
    return result
}

/**
 * Dibuja la firma sobre la línea indicada
 */
fun drawFirmaSobreLinea(
    canvas: Canvas,
    firma: FirmaDecoded,
    lineX: Float,
    lineY: Float,
    targetWidth: Float = 180f,
    targetHeight: Float = 80f
) {
    try {
        val decodedBytes = Base64.decode(firma.base64, Base64.DEFAULT)
        val original = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size) ?: return
        val cleaned = removeWhiteBackground(original)

        // Calcular posición vertical con altura visual
        val firmaOffsetY = lineY - targetHeight * (1f - firma.ratio)

        // Dibujar el bitmap original en un área más pequeña (sin escalar el bitmap)
        val destRect = RectF(lineX, firmaOffsetY, lineX + targetWidth, firmaOffsetY + targetHeight)
        canvas.drawBitmap(cleaned, null, destRect, null)
    } catch (e: Exception) {
        // no dibuja nada si falla
    }
}

