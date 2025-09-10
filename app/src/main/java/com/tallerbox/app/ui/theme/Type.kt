package com.tallerbox.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.tallerbox.app.R

val InterFamily = FontFamily(
    Font(R.font.inter_light,    weight = FontWeight.Light),
    Font(R.font.inter_regular,  weight = FontWeight.Normal),
    Font(R.font.inter_medium,   weight = FontWeight.Medium),
    Font(R.font.inter_semibold, weight = FontWeight.SemiBold),
    Font(R.font.inter_bold,     weight = FontWeight.Bold)
)

// 2. Define tu Typography usando ese FontFamily
val Typography = Typography(
    headlineMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight  = FontWeight.SemiBold,
        fontSize    = 32.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight  = FontWeight.Normal,
        fontSize    = 16.sp
    )
    // Puedes sobrescribir otros estilos si los necesitas
)