package com.tallerbox.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.tallerbox.app.R

// Paleta basada en azul Alexa
private val AlexaColorScheme = lightColorScheme(
    primary = Color(0xFF1296DB),
    onPrimary = Color.White,
    secondary = Color(0xFF5AC8FA),
    onSecondary = Color.Black,
    tertiary = Color(0xFFE0E0E0),
    onTertiary = Color.Black,
    background = Color(0xFFFFFFFF),
    onBackground = Color.Black,
    surface = Color(0xFFF5F5F5),
    onSurface = Color(0xFF333333)
)

// Tipografía Inter
private val Inter = FontFamily(
    Font(R.font.inter_light, FontWeight.Light),
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold)
)

private val TallerBoxTypography = Typography(
    displayLarge = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 32.sp),
    headlineSmall = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 20.sp),
    bodyLarge = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 16.sp),
    labelSmall = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 12.sp)
)

@Composable
fun TallerBoxTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AlexaColorScheme,
        typography = TallerBoxTypography,
        content = content
    )
}
