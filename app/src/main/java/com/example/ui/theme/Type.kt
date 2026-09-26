package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.R

fun getFontFamily(fontName: String): FontFamily {
    return FontFamily.SansSerif
}

val PoppinsFontFamily by lazy { getFontFamily("Poppins") }
val BubblegumFontFamily by lazy { getFontFamily("Bubblegum Sans") }
val PlayfairFontFamily by lazy { getFontFamily("Playfair Display") }
val MontserratFontFamily by lazy { getFontFamily("Montserrat") }
val CaveatFontFamily by lazy { getFontFamily("Caveat") }
val PlusJakartaSansFamily by lazy { getFontFamily("Plus Jakarta Sans") }

fun getTypographyForFont(fontName: String): Typography {
    val fontFamily = when (fontName) {
        "Poppins" -> PoppinsFontFamily
        "Bubblegum" -> BubblegumFontFamily
        "Playfair" -> PlayfairFontFamily
        "Montserrat" -> MontserratFontFamily
        "Caveat" -> CaveatFontFamily
        "Plus Jakarta Sans" -> PlusJakartaSansFamily
        else -> PlusJakartaSansFamily
    }

    return Typography(
        displayLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        ),
        titleLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        ),
        labelLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    )
}

// Default typography for initial load or preview
val Typography = getTypographyForFont("Plus Jakarta Sans")

// App එකේ Post Actions (Like, Comment, Share) සහ Titles සඳහා Modern Styles
val PostActionTextStyle = TextStyle(
    fontFamily = PlusJakartaSansFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 14.sp,
    color = Color(0xFFF8FAFC) // Clean light text
)

val PostTitleStyle = TextStyle(
    fontFamily = PlusJakartaSansFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 18.sp,
    color = Color(0xFFF8FAFC)
)
