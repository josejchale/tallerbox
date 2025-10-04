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
import java.io.ByteArrayOutputStream
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.core.graphics.createBitmap

@Composable
fun FirmaPad(
    modifier: Modifier = Modifier,
    onFirmaConfirmada: (String) -> Unit
) {
    // Lista de puntos para dibujar en Compose Canvas (visual)
    var puntos by remember { mutableStateOf(listOf<Offset>()) }

    // También mantenemos un android.graphics.Path para exportar a Bitmap posteriormente
    val androidPath = remember { AndroidPath() }

    val strokeWidthPx = with(LocalDensity.current) { 2.dp.toPx() }

    Column(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {
        // Área visible donde el usuario firma
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
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
            // Pintar la línea visual en Compose Canvas usando Path de Compose
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

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = {
                // limpiar la vista y el path android
                puntos = emptyList()
                androidPath.reset()
            }) {
                Text("Limpiar")
            }

            Button(onClick = {
                // Exportar androidPath a bitmap usando android Canvas
                val width = 800
                val height = 200
                val bitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888);                val canvas = AndroidCanvas(bitmap)
                // fondo blanco
                canvas.drawColor(android.graphics.Color.WHITE)

                val paint = AndroidPaint().apply {
                    color = android.graphics.Color.BLACK
                    strokeWidth = strokeWidthPx
                    style = AndroidPaint.Style.STROKE
                    isAntiAlias = true
                    strokeJoin = AndroidPaint.Join.ROUND
                    strokeCap = AndroidPaint.Cap.ROUND
                }

                // Si no hay trazos, androidPath será vacío; aún así no fallará
                canvas.drawPath(androidPath, paint)

                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                val base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
                onFirmaConfirmada(base64)
            }) {
                Text("Confirmar firma")
            }
        }
    }
}
