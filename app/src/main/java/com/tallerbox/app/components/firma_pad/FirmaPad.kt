package com.tallerbox.app.components.firma_pad

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint as AndroidPaint
import android.graphics.Path as AndroidPath
import android.util.Base64
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.core.graphics.createBitmap
import java.io.ByteArrayOutputStream

private const val SIGNATURE_HEIGHT_DP = 200

@Composable
fun FirmaPad(
    modifier: Modifier = Modifier,
    onFirmaConfirmada: (String) -> Unit
) {
    val density = LocalDensity.current
    val strokeWidthPx = with(density) { 2.dp.toPx() }

    // 🟢 Cada trazo es una lista de puntos
    var strokes by remember { mutableStateOf(listOf<List<Offset>>()) }
    var currentStroke by remember { mutableStateOf<List<Offset>>(emptyList()) }

    Column(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(SIGNATURE_HEIGHT_DP.dp)
                .background(Color.White)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentStroke = listOf(offset)
                        },
                        onDrag = { change, _ ->
                            currentStroke = currentStroke + change.position
                        },
                        onDragEnd = {
                            strokes = strokes + listOf(currentStroke)
                            currentStroke = emptyList()
                        }
                    )
                }
        ) {
            val w = size.width
            val h = size.height

            // 🔲 BORDE
            drawRect(
                color = Color.Gray,
                size = size,
                style = Stroke(width = 2f)
            )

            // ➖ LÍNEA GUÍA
            val guideY = h * 0.75f
            drawLine(
                color = Color.DarkGray,
                start = Offset(16f, guideY),
                end = Offset(w - 16f, guideY),
                strokeWidth = 1.5f
            )

            // ✍️ DIBUJAR TODOS LOS TRAZOS (incluido el actual)
            (strokes + listOf(currentStroke)).forEach { stroke ->
                if (stroke.size > 1) {
                    val path = Path().apply {
                        moveTo(stroke.first().x, stroke.first().y)
                        stroke.drop(1).forEach { lineTo(it.x, it.y) }
                    }
                    drawPath(
                        path = path,
                        color = Color.Black,
                        style = Stroke(
                            width = strokeWidthPx,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = {
                strokes = emptyList()
                currentStroke = emptyList()
            }) {
                Text("Limpiar")
            }

            Button(onClick = {
                val widthPx = with(density) { 800.dp.toPx().toInt() }
                val heightPx = with(density) { SIGNATURE_HEIGHT_DP.dp.toPx().toInt() }

                // 🟢 BITMAP TRANSPARENTE
                val bitmap = createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
                val canvas = AndroidCanvas(bitmap)

                val paint = AndroidPaint().apply {
                    color = android.graphics.Color.BLACK
                    strokeWidth = strokeWidthPx
                    style = AndroidPaint.Style.STROKE
                    isAntiAlias = true
                    strokeJoin = AndroidPaint.Join.ROUND
                    strokeCap = AndroidPaint.Cap.ROUND
                }

                // Convertir strokes → AndroidPath
                strokes.forEach { stroke ->
                    if (stroke.size > 1) {
                        val path = AndroidPath().apply {
                            moveTo(stroke.first().x, stroke.first().y)
                            stroke.drop(1).forEach { lineTo(it.x, it.y) }
                        }
                        canvas.drawPath(path, paint)
                    }
                }

                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

                val base64 = Base64.encodeToString(
                    outputStream.toByteArray(),
                    Base64.NO_WRAP
                )

                onFirmaConfirmada(base64)
            }) {
                Text("Confirmar firma")
            }
        }
    }
}
