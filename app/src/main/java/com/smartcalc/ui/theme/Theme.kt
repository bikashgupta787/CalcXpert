package com.smartcalc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ── Palette ──────────────────────────────────────────────────────────────────
val Primary        = Color(0xFF0A84FF)   // iOS-style blue
val PrimaryVariant = Color(0xFF0060D0)
val Secondary      = Color(0xFF30D158)   // Mint green accent
val Tertiary       = Color(0xFFFF9F0A)   // Amber accent

// Dark surface layers (Material You depth)
val Surface0 = Color(0xFF0D0D0F)   // true-black bg
val Surface1 = Color(0xFF1C1C1E)   // card bg
val Surface2 = Color(0xFF2C2C2E)   // elevated card / button bg
val Surface3 = Color(0xFF3A3A3C)   // input / chip bg

val OnSurface   = Color(0xFFFFFFFF)
val OnSurfaceDim = Color(0xFF8E8E93)

// Light theme surfaces
val LightSurface0  = Color(0xFFF2F2F7)
val LightSurface1  = Color(0xFFFFFFFF)
val LightSurface2  = Color(0xFFE5E5EA)
val LightSurface3  = Color(0xFFD1D1D6)
val LightOnSurface  = Color(0xFF000000)
val LightOnSurfaceDim = Color(0xFF6C6C70)

// ── Dark Color Scheme ─────────────────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary            = Primary,
    onPrimary          = Color.White,
    primaryContainer   = PrimaryVariant,
    secondary          = Secondary,
    tertiary           = Tertiary,
    background         = Surface0,
    surface            = Surface1,
    surfaceVariant     = Surface2,
    onBackground       = OnSurface,
    onSurface          = OnSurface,
    onSurfaceVariant   = OnSurfaceDim,
    outline            = Surface3,
)

// ── Light Color Scheme ────────────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary            = Primary,
    onPrimary          = Color.White,
    primaryContainer   = Color(0xFFD0E8FF),
    secondary          = Color(0xFF1AAB42),
    tertiary           = Color(0xFFB86800),
    background         = LightSurface0,
    surface            = LightSurface1,
    surfaceVariant     = LightSurface2,
    onBackground       = LightOnSurface,
    onSurface          = LightOnSurface,
    onSurfaceVariant   = LightOnSurfaceDim,
    outline            = LightSurface3,
)

// ── Typography ────────────────────────────────────────────────────────────────
// Using system default for now; swap with a custom font if bundled
val SmartCalcTypography = Typography(
    displayLarge  = TextStyle(fontWeight = FontWeight.Light,   fontSize = 57.sp, letterSpacing = (-0.25).sp),
    displayMedium = TextStyle(fontWeight = FontWeight.Light,   fontSize = 45.sp),
    displaySmall  = TextStyle(fontWeight = FontWeight.Normal,  fontSize = 36.sp),
    headlineLarge = TextStyle(fontWeight = FontWeight.SemiBold,fontSize = 32.sp),
    headlineMedium= TextStyle(fontWeight = FontWeight.SemiBold,fontSize = 28.sp),
    headlineSmall = TextStyle(fontWeight = FontWeight.SemiBold,fontSize = 24.sp),
    titleLarge    = TextStyle(fontWeight = FontWeight.Bold,    fontSize = 22.sp),
    titleMedium   = TextStyle(fontWeight = FontWeight.SemiBold,fontSize = 16.sp),
    titleSmall    = TextStyle(fontWeight = FontWeight.Medium,  fontSize = 14.sp),
    bodyLarge     = TextStyle(fontWeight = FontWeight.Normal,  fontSize = 16.sp),
    bodyMedium    = TextStyle(fontWeight = FontWeight.Normal,  fontSize = 14.sp),
    bodySmall     = TextStyle(fontWeight = FontWeight.Normal,  fontSize = 12.sp),
    labelLarge    = TextStyle(fontWeight = FontWeight.Medium,  fontSize = 14.sp),
    labelMedium   = TextStyle(fontWeight = FontWeight.Medium,  fontSize = 12.sp),
    labelSmall    = TextStyle(fontWeight = FontWeight.Medium,  fontSize = 11.sp),
)

// ── Theme Composable ──────────────────────────────────────────────────────────
@Composable
fun SmartCalcTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = SmartCalcTypography,
        content     = content
    )
}
