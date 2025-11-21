package com.tallerbox.app.components.firma_pad

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint as AndroidPaint
import android.graphics.Path as AndroidPath
import android.util.Base64
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
    var trazos by remember { mutableStateOf(listOf<List<Offset>>()) }
    var trazoActual by remember { mutableStateOf(listOf<Offset>()) }

    val androidPath = remember { AndroidPath() }
    val strokeWidthPx = with(LocalDensity.current) { 2.dp.toPx() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Firma del cliente", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(Color.White)
                .border(2.dp, Color.Gray)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                trazoActual = listOf(offset)
                                androidPath.moveTo(offset.x, offset.y)
                            },
                            onDragEnd = {
                                trazos = trazos + listOf(trazoActual)
                                trazoActual = emptyList()
                            },
                            onDrag = { change, _ ->
                                val pos = change.position
                                trazoActual = trazoActual + pos
                                androidPath.lineTo(pos.x, pos.y)
                            }
                        )
                    }
            ) {
                val guideY = size.height * 0.8f
                drawLine(
                    color = Color.DarkGray,
                    start = Offset(0f, guideY),
                    end = Offset(size.width, guideY),
                    strokeWidth = 1f
                )

                (trazos + listOf(trazoActual)).forEach { trazo ->
                    if (trazo.size > 1) {
                        val path = Path().apply {
                            moveTo(trazo.first().x, trazo.first().y)
                            trazo.drop(1).forEach { lineTo(it.x, it.y) }
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
        }


        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = {
                trazos = emptyList()
                trazoActual = emptyList()
                androidPath.reset()
            }) {
                Text("Limpiar")
            }

            Button(onClick = {
                val width = 640
                val height = 320
                val bitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888)
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

