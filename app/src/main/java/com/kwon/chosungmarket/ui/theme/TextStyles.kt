package com.kwon.chosungmarket.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.kwon.chosungmarket.R

object TextStyles {
    // Define custom font families
    private val NanumSquareRound = FontFamily(
        Font(R.font.nanumsquareroundeb, FontWeight.ExtraBold),
        Font(R.font.nanumsquareroundb, FontWeight.Bold)
    )

    private val Pretendard = FontFamily(
        Font(R.font.pretendard_regular, FontWeight.Normal),
        Font(R.font.pretendard_medium, FontWeight.Medium),
        Font(R.font.pretendard_semibold, FontWeight.SemiBold),
        Font(R.font.pretendard_bold, FontWeight.Bold)
    )

    // Title Styles
    fun TitleLargeEB(
        color: Color = Colors.CompColorTextPrimary,
        fontSize: Int = 18,
        lineHeight: Int = 24,
        decoration: TextDecoration = TextDecoration.None
    ) = TextStyle(
        fontFamily = NanumSquareRound,
        fontWeight = FontWeight.ExtraBold,
        fontSize = fontSize.sp,
        lineHeight = lineHeight.sp,
        color = color,
        textDecoration = decoration
    )

    fun TitleMediumB(
        color: Color = Colors.CompColorTextPrimary,
        fontSize: Int = 18,
        lineHeight: Int = 24,
        decoration: TextDecoration = TextDecoration.None
    ) = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = fontSize.sp,
        lineHeight = lineHeight.sp,
        color = color,
        textDecoration = decoration
    )

    fun TitleSmallB(
        color: Color = Colors.CompColorTextPrimary,
        fontSize: Int = 16,
        lineHeight: Int = 24,
        decoration: TextDecoration = TextDecoration.None
    ) = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = fontSize.sp,
        lineHeight = lineHeight.sp,
        color = color,
        textDecoration = decoration
    )

    fun TitleXsmallB(
        color: Color = Colors.CompColorTextPrimary,
        fontSize: Int = 14,
        lineHeight: Int = 20,
        decoration: TextDecoration = TextDecoration.None
    ) = TextStyle(
        fontFamily = NanumSquareRound,
        fontWeight = FontWeight.Bold,
        fontSize = fontSize.sp,
        lineHeight = lineHeight.sp,
        color = color,
        textDecoration = decoration
    )

    // Leading Style
    fun LeadingMediumEB(
        color: Color = Colors.CompColorTextPrimary,
        fontSize: Int = 20,
        lineHeight: Int = 28,
        decoration: TextDecoration = TextDecoration.None
    ) = TextStyle(
        fontFamily = NanumSquareRound,
        fontWeight = FontWeight.ExtraBold,
        fontSize = fontSize.sp,
        lineHeight = lineHeight.sp,
        color = color,
        textDecoration = decoration
    )

    // Body Styles
    fun BodyLargeB(
        color: Color = Colors.CompColorTextPrimary,
        fontSize: Int = 20,
        lineHeight: Int = 28,
        decoration: TextDecoration = TextDecoration.None
    ) = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = fontSize.sp,
        lineHeight = lineHeight.sp,
        color = color,
        textDecoration = decoration
    )

    fun BodyMediumM(
        color: Color = Colors.CompColorTextPrimary,
        fontSize: Int = 18,
        lineHeight: Int = 24,
        decoration: TextDecoration = TextDecoration.None
    ) = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = fontSize.sp,
        lineHeight = lineHeight.sp,
        color = color,
        textDecoration = decoration
    )

    fun BodySmallB(
        color: Color = Colors.CompColorTextPrimary,
        fontSize: Int = 16,
        lineHeight: Int = 24,
        decoration: TextDecoration = TextDecoration.None
    ) = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = fontSize.sp,
        lineHeight = lineHeight.sp,
        color = color,
        textDecoration = decoration
    )

    fun BodySmallSB(
        color: Color = Colors.CompColorTextPrimary,
        fontSize: Int = 16,
        lineHeight: Int = 24,
        decoration: TextDecoration = TextDecoration.None
    ) = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = fontSize.sp,
        lineHeight = lineHeight.sp,
        color = color,
        textDecoration = decoration
    )

    fun BodySmallR(
        color: Color = Colors.CompColorTextPrimary,
        fontSize: Int = 16,
        lineHeight: Int = 24,
        decoration: TextDecoration = TextDecoration.None
    ) = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = fontSize.sp,
        lineHeight = lineHeight.sp,
        color = color,
        textDecoration = decoration
    )

    // Sub Styles
    fun SubXlargeSB(
        color: Color = Colors.CompColorTextPrimary,
        fontSize: Int = 14,
        lineHeight: Int = 20,
        decoration: TextDecoration = TextDecoration.None
    ) = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = fontSize.sp,
        lineHeight = lineHeight.sp,
        color = color,
        textDecoration = decoration
    )

    fun SubLargeR(
        color: Color = Colors.CompColorTextPrimary,
        fontSize: Int = 14,
        lineHeight: Int = 20,
        decoration: TextDecoration = TextDecoration.None
    ) = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = fontSize.sp,
        lineHeight = lineHeight.sp,
        color = color,
        textDecoration = decoration
    )

    fun SubMediumSB(
        color: Color = Colors.CompColorTextPrimary,
        fontSize: Int = 12,
        lineHeight: Int = 16,
        decoration: TextDecoration = TextDecoration.None
    ) = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = fontSize.sp,
        lineHeight = lineHeight.sp,
        color = color,
        textDecoration = decoration
    )

    fun SubMediumR(
        color: Color = Colors.CompColorTextPrimary,
        fontSize: Int = 12,
        lineHeight: Int = 16,
        decoration: TextDecoration = TextDecoration.None
    ) = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = fontSize.sp,
        lineHeight = lineHeight.sp,
        color = color,
        textDecoration = decoration
    )

    fun SubSmallR(
        color: Color = Colors.CompColorTextPrimary,
        fontSize: Int = 10,
        lineHeight: Int = 14,
        decoration: TextDecoration = TextDecoration.None
    ) = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = fontSize.sp,
        lineHeight = lineHeight.sp,
        color = color,
        textDecoration = decoration
    )
}