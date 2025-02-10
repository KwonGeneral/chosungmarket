package com.kwon.chosungmarket.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

private val LightColorScheme = lightColorScheme(
    primary = Colors.SysColorPrimary,
    secondary = Colors.SysColorSecondary,
    tertiary = Colors.SysColorSecondarySurface,
    background = Colors.CompColorPageDefaultBackground,
    surface = Colors.CompColorContainerBoxBackground,
    onPrimary = Colors.SysColorOnPrimary,
    onSecondary = Colors.SysColorOnPrimary,
    onTertiary = Colors.SysColorOnPrimary,
    onBackground = Colors.CompColorTextPrimary,
    onSurface = Colors.CompColorTextPrimary
)

// Typography 정의
private val AppTypography = Typography(
    // Display styles
    displayLarge = TextStyles.TitleLargeEB(),
    displayMedium = TextStyles.TitleMediumB(),
    displaySmall = TextStyles.TitleSmallB(),

    // Headline styles
    headlineLarge = TextStyles.BodyLargeB(),
    headlineMedium = TextStyles.BodyMediumM(),
    headlineSmall = TextStyles.BodySmallB(),

    // Title styles
    titleLarge = TextStyles.BodySmallSB(),
    titleMedium = TextStyles.SubXlargeSB(),
    titleSmall = TextStyles.SubLargeR(),

    // Body styles
    bodyLarge = TextStyles.BodySmallR(),
    bodyMedium = TextStyles.SubMediumSB(),
    bodySmall = TextStyles.SubMediumR(),

    // Label styles
    labelLarge = TextStyles.SubLargeR(),
    labelMedium = TextStyles.SubMediumR(),
    labelSmall = TextStyles.SubSmallR()
)

// Custom LocalComposition for our theme
val LocalAppColors = staticCompositionLocalOf { Colors }
val LocalAppTypography = staticCompositionLocalOf { TextStyles }

@Composable
fun ChosungmarketTheme(
    content: @Composable () -> Unit
) {
    // Material3 ColorScheme 설정
    val colorScheme = LightColorScheme

    // CompositionLocalProvider를 통해 커스텀 Colors와 Typography 제공
    CompositionLocalProvider(
        LocalAppColors provides Colors,
        LocalAppTypography provides TextStyles
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}

// Theme 사용을 위한 확장 함수들
object AppTheme {
    val colors: Colors
        @Composable
        get() = LocalAppColors.current

    val styles: TextStyles
        @Composable
        get() = LocalAppTypography.current
}