package com.chiefdenis.carsart.ui.theme

import android.os.Build
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.platform.LocalContext
import com.chiefdenis.carsart.data.repository.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight
)

// Custom motion scheme for expressive animations
@Immutable
data class CarSartMotionTokens(
    val durationShort1: Int = 150,
    val durationShort2: Int = 200,
    val durationShort3: Int = 250,
    val durationShort4: Int = 300,
    val durationMedium1: Int = 350,
    val durationMedium2: Int = 400,
    val durationMedium3: Int = 450,
    val durationMedium4: Int = 500,
    val durationLong1: Int = 550,
    val durationLong2: Int = 600,
    val durationLong3: Int = 650,
    val durationLong4: Int = 700,
    
    val easingStandard: androidx.compose.animation.core.Easing = EaseInOut,
    val easingStandardDecelerate: androidx.compose.animation.core.Easing = EaseInOut,
    val easingStandardAccelerate: androidx.compose.animation.core.Easing = EaseInOut,
    val easingEmphasized: androidx.compose.animation.core.Easing = EaseOutBack,
    val easingEmphasizedDecelerate: androidx.compose.animation.core.Easing = EaseOutBack,
    val easingEmphasizedAccelerate: androidx.compose.animation.core.Easing = EaseInOut,
    val easingLegacy: androidx.compose.animation.core.Easing = EaseInOut
)

object CarSartMotion {
    val tokens = CarSartMotionTokens()
    
    // Standard animation specs
    val shortTween = tween<Float>(durationMillis = tokens.durationShort2, easing = tokens.easingStandard)
    val mediumTween = tween<Float>(durationMillis = tokens.durationMedium2, easing = tokens.easingStandard)
    val longTween = tween<Float>(durationMillis = tokens.durationLong2, easing = tokens.easingStandard)
    
    // Emphasized animation specs for important transitions
    val emphasizedShortTween = tween<Float>(durationMillis = tokens.durationShort3, easing = tokens.easingEmphasized)
    val emphasizedMediumTween = tween<Float>(durationMillis = tokens.durationMedium3, easing = tokens.easingEmphasized)
    val emphasizedLongTween = tween<Float>(durationMillis = tokens.durationLong3, easing = tokens.easingEmphasized)
    
    // Container transform durations
    val containerTransformDuration = tokens.durationMedium4
    val sharedAxisDuration = tokens.durationMedium2
    val fadeThroughDuration = tokens.durationShort3
}

@Composable
fun CarSARTTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    // Dynamic color is available on Android S+ and is part of Material 3.
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val systemDarkTheme = isSystemInDarkTheme()
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> systemDarkTheme
    }
    
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
