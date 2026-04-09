package com.example.demo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import demo.core.ui.generated.resources.NotoSerif_Bold
import demo.core.ui.generated.resources.NotoSerif_Italic
import demo.core.ui.generated.resources.NotoSerif_Light
import demo.core.ui.generated.resources.NotoSerif_Medium
import demo.core.ui.generated.resources.NotoSerif_Regular
import demo.core.ui.generated.resources.Res
import org.jetbrains.compose.resources.Font

@Composable
fun appTypography(): Typography {
    val notoFamily = FontFamily(
        Font(Res.font.NotoSerif_Regular, weight = FontWeight.Normal),
        Font(Res.font.NotoSerif_Bold, weight = FontWeight.Bold),
        Font(Res.font.NotoSerif_Medium, weight = FontWeight.Medium),
        Font(Res.font.NotoSerif_Italic, weight = FontWeight.Normal, style = FontStyle.Italic),
        Font(Res.font.NotoSerif_Light, weight = FontWeight.Light),
        // add Medium/Bold if present
    )
    return Typography(
        bodySmall = TextStyle(fontFamily = notoFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp),
        bodyMedium = TextStyle(fontFamily = notoFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp),
        bodyLarge = TextStyle(fontFamily = notoFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp),
        titleLarge = TextStyle(fontFamily = notoFamily, fontWeight = FontWeight.Medium, fontSize = 22.sp),
        headlineSmall = TextStyle(fontFamily = notoFamily, fontWeight = FontWeight.Bold, fontSize = 24.sp),
    )
}