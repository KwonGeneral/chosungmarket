package com.kwon.chosungmarket.ui.theme

import androidx.compose.ui.graphics.Color


object Colors {
    val RefColorBlue40 = Color(0xFF5A5AF6)
    val RefColorBlue50 = Color(0xFF7070FF)
    val RefColorBlue60 = Color(0xFF8A8AFF)
    val RefColorBlue70 = Color(0xFFA8A8FF)
    val RefColorBlue95 = Color(0xFFEBEBFF)
    val RefColorGray20 = Color(0xFF2D3439)
    val RefColorGray30 = Color(0xFF434E56)
    val RefColorGray40 = Color(0xFF5A6872)
    val RefColorGray50 = Color(0xFF70838F)
    val RefColorGray60 = Color(0xFF879BAB)
    val RefColorGray70 = Color(0xFFA5B4C0)
    val RefColorGray80 = Color(0xFFC3CDD5)
    val RefColorGray90 = Color(0xFFE6EBEF)
    val RefColorGray95 = Color(0xFFF3F5F7)
    val RefColorMint40 = Color(0xFF45B5A6)
    val RefColorMint50 = Color(0xFF67CABD)
    val RefColorMint60 = Color(0xFF81D6CB)
    val RefColorMint80 = Color(0xFFB6EDE5)
    val RefColorMint90 = Color(0xFFD6F5F0)
    val RefColorMint95 = Color(0xFFEAFAF8)
    val RefColorOrange50 = Color(0xFFFF881A)
    val RefColorOrange60 = Color(0xFFFF9933)
    val RefColorOrange70 = Color(0xFFFFB164)
    val RefColorOrange95 = Color(0xFFFFF5EB)
    val RefColorRed50 = Color(0xFFDA392B)
    val RefColorRed60 = Color(0xFFEB5547)
    val RefColorRed95 = Color(0xFFFDEEED)
    val RefColorTransparentBlack20 = Color(0xCC161A1D)
    val RefColorTransparentBlack30 = Color(0xB2161A1D)
    val RefColorTransparentBlack50 = Color(0x7F161A1D)
    val RefColorTransparentBlack60 = Color(0x66161A1D)
    val RefColorTransparentBlack80 = Color(0x33161A1D)
    val RefColorTransparentBlack90 = Color(0x19161A1D)
    val RefColorTransparentBlack95 = Color(0x0C161A1D)
    val RefColorTransparentWhite10 = Color(0xE5FFFFFF)
    val RefColorTransparentWhite40 = Color(0x99FFFFFF)
    val RefColorTransparentGray95 = Color(0x99F3F5F7)
    val RefColorWhite = Color(0xFFFFFFFF)
    val RefColorBlock = Color(0xFF000000)

    // System com.kwon.chosungmarket.ui.theme.Colors
    val SysColorPrimary = RefColorBlue50
    val SysColorPrimaryContainer = RefColorBlue95
    val SysColorPrimarySurface = RefColorBlue60
    val SysColorCaution = RefColorOrange50
    val SysColorCautionContainer = RefColorOrange95
    val SysColorCautionSurface = RefColorOrange60
    val SysColorInformative = RefColorBlue50
    val SysColorInformativeContainer = RefColorBlue95
    val SysColorInformativeSurface = RefColorBlue60
    val SysColorNegative = RefColorRed50
    val SysColorNegativeContainer = RefColorRed95
    val SysColorNegativeSurface = RefColorRed60
    val SysColorNeutral100 = RefColorWhite
    val SysColorNeutral20 = RefColorGray20
    val SysColorNeutral30 = RefColorGray30
    val SysColorNeutral40 = RefColorGray40
    val SysColorNeutral50 = RefColorGray50
    val SysColorNeutral60 = RefColorGray60
    val SysColorNeutral70 = RefColorGray70
    val SysColorNeutral80 = RefColorGray80
    val SysColorNeutral90 = RefColorGray90
    val SysColorNeutral95 = RefColorGray95
    val SysColorOnPrimary = RefColorWhite
    val SysColorPositive = RefColorMint40
    val SysColorPositiveContainer = RefColorMint95
    val SysColorPositiveSurface = RefColorMint50
    val SysColorSecondary = RefColorMint40
    val SysColorSecondaryContainer = RefColorMint95
    val SysColorSecondarySurface = RefColorMint50

    // Component com.kwon.chosungmarket.ui.theme.Colors - Brand
    val CompColorBrand = SysColorPrimary

    // Component com.kwon.chosungmarket.ui.theme.Colors - Button
    val CompColorButtonBackground = SysColorPrimary
    val CompColorButtonBorder = SysColorPrimarySurface
    val CompColorButtonText = SysColorOnPrimary
    val CompColorButtonCancelBackground = SysColorNeutral95
    val CompColorButtonCancelBorder = SysColorNeutral80
    val CompColorButtonCancelText = SysColorNeutral40

    // Component com.kwon.chosungmarket.ui.theme.Colors - Text
    val CompColorTextPrimary = SysColorNeutral20
    val CompColorTextDescription = SysColorNeutral60
    val CompColorTextWarning = SysColorNegative

    // Component com.kwon.chosungmarket.ui.theme.Colors - Text Button
    val CompColorTextButtonBackground = SysColorNeutral100
    val CompColorTextButtonBorder = SysColorNeutral90
    val CompColorTextButtonText = SysColorPrimary
    val CompColorTextButtonCancelBackground = SysColorNeutral100
    val CompColorTextButtonCancelBorder = SysColorNeutral90
    val CompColorTextButtonCancelText = SysColorNeutral40

    // Component com.kwon.chosungmarket.ui.theme.Colors - Page
    val CompColorPageDefaultBackground = SysColorNeutral95
    val CompColorPageDetailBackground = SysColorNeutral100

    // Component com.kwon.chosungmarket.ui.theme.Colors - Container Box
    val CompColorContainerBoxBackground = SysColorNeutral100
    val CompColorContainerBoxBorder = SysColorNeutral90
    val CompColorContainerBoxEmphasisBackground = SysColorPrimaryContainer
    val CompColorContainerBoxEmphasisBorder = SysColorPrimarySurface

    // Component com.kwon.chosungmarket.ui.theme.Colors - Icon Label
    val CompColorIconLabelPrimary = SysColorNeutral20
    val CompColorIconLabelBrandPrimary = CompColorBrand
    val CompColorIconLabelBrandSecondary = SysColorNeutral50
    val CompColorIconLabelBrandTertiary = SysColorNeutral70
    val CompColorIconLabelBrandQuaternary = SysColorNeutral90

    // Component com.kwon.chosungmarket.ui.theme.Colors - Line
    val CompColorLinePrimary = SysColorNeutral80
    val CompColorLineSecondary = SysColorNeutral90
    val CompColorLineTertiary = SysColorNeutral95
    val CompColorLineQuaternary = SysColorNeutral50
    val CompColorLineQuinary = SysColorNeutral70

    // Component com.kwon.chosungmarket.ui.theme.Colors - Icon Button
    val CompColorIconButtonBackground = SysColorNeutral100
    val CompColorIconButtonBorder = SysColorNeutral90
    val CompColorIconButtonIcon = SysColorNeutral50
    val CompColorIconButtonText = SysColorNeutral20
    val CompColorIconButtonCancelBackground = SysColorNeutral100
    val CompColorIconButtonCancelBorder = SysColorNeutral90
    val CompColorIconButtonCancelIcon = SysColorNeutral50
    val CompColorIconButtonCancelText = SysColorNeutral20
}