package com.tallerbox.app.helper

data class FirmaDecoded(val ratio: Float, val base64: String)

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
