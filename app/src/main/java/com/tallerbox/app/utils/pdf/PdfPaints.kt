package com.tallerbox.app.utils.pdf

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface

object PdfPaints {

    val headerTitle = Paint().apply {
        textSize = 14f
        typeface = Typeface.DEFAULT_BOLD
        color = Color.BLACK
        isAntiAlias = true
    }

    val headerInfo = Paint().apply {
        textSize = 10f
        color = Color.BLACK
        isAntiAlias = true
    }

    val sectionTitle = Paint().apply {
        textSize = 11f
        typeface = Typeface.DEFAULT_BOLD
        color = Color.BLACK
        isAntiAlias = true
    }

    val boxTitle = Paint().apply {
        textSize = 10f
        typeface = Typeface.DEFAULT_BOLD
        color = Color.BLACK
        isAntiAlias = true
    }

    val tableHeader = Paint().apply {
        textSize = 8f
        typeface = Typeface.DEFAULT_BOLD
        color = Color.DKGRAY
        isAntiAlias = true
    }

    val body = Paint().apply {
        textSize = 8f
        color = Color.BLACK
        isAntiAlias = true
    }

    val bodyBold = Paint(body).apply {
        typeface = Typeface.DEFAULT_BOLD
        color = Color.BLACK
        isAntiAlias = true
    }

    val line = Paint().apply {
        color = Color.BLACK
        strokeWidth = 1f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    val paintX = Paint().apply {
        color = Color.BLACK
        textSize = 8f
        typeface = Typeface.DEFAULT_BOLD
        isAntiAlias = true
    }
}
