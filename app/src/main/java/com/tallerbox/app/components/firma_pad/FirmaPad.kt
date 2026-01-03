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
    var puntos by remember { mutableStateOf(listOf<Offset>()) }
    val androidPath = remember { AndroidPath() }

    val density = LocalDensity.current
    val strokeWidthPx = with(density) { 2.dp.toPx() }

    Column(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(SIGNATURE_HEIGHT_DP.dp)
                .background(Color.White)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            puntos = puntos + offset
                            androidPath.moveTo(offset.x, offset.y)
                        },
                        onDrag = { change, _ ->
                            val pos = change.position
                            puntos = puntos + pos
                            androidPath.lineTo(pos.x, pos.y)
                        }
                    )
                }
        ) {
            val w = size.width
            val h = size.height

            // 🟦 CONTORNO DEL ÁREA DE FIRMA
            drawRect(
                color = Color.Gray,
                size = size,
                style = Stroke(width = 2f)
            )

            // ➖ LÍNEA GUÍA DE FIRMA (75% de altura)
            val guideY = h * 0.75f
            drawLine(
                color = Color.DarkGray,
                start = Offset(16f, guideY),
                end = Offset(w - 16f, guideY),
                strokeWidth = 1.5f
            )

            // ✍️ TRAZOS DE LA FIRMA
            if (puntos.isNotEmpty()) {
                val composePath = Path().apply {
                    moveTo(puntos.first().x, puntos.first().y)
                    puntos.forEach { lineTo(it.x, it.y) }
                }
                drawPath(
                    path = composePath,
                    color = Color.Black,
                    style = Stroke(
                        width = strokeWidthPx,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = {
                puntos = emptyList()
                androidPath.reset()
            }) {
                Text("Limpiar")
            }

            Button(onClick = {
                // 🔄 EXPORTAR EXACTAMENTE EL MISMO ÁREA
                val widthPx = with(density) { 800.dp.toPx().toInt() }
                val heightPx = with(density) { SIGNATURE_HEIGHT_DP.dp.toPx().toInt() }

                val bitmap = createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
                val canvas = AndroidCanvas(bitmap)
                canvas.drawColor(android.graphics.Color.WHITE)

                val paint = AndroidPaint().apply {
                    color = android.graphics.Color.BLACK
                    strokeWidth = strokeWidthPx
                    style = AndroidPaint.Style.STROKE
                    isAntiAlias = true
                    strokeJoin = AndroidPaint.Join.ROUND
                    strokeCap = AndroidPaint.Cap.ROUND
                }

                // ✍️ SOLO LA FIRMA (SIN BORDE NI LÍNEA)
                canvas.drawPath(androidPath, paint)

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
