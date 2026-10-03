package com.ivy.design.l0_system

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.ivy.base.legacy.Theme
import com.ivy.design.api.IvyDesign
import com.ivy.design.system.IvyMaterial3Theme
import com.ivy.design.system.supportsDynamicColor

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val LocalIvyColors = compositionLocalOf<IvyColors> { error("No IvyColors") }

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val LocalIvyTypography = compositionLocalOf<IvyTypography> { error("No IvyTypography") }

@Deprecated("Old design system. Use `:ivy-design` and Material3")
val LocalIvyShapes = compositionLocalOf<IvyShapes> { error("No IvyShapes") }

@Deprecated("Old design system. Use `:ivy-design` and Material3")
object UI {
    val colors: IvyColors
        @Composable
        @ReadOnlyComposable
        get() = LocalIvyColors.current

    val typo: IvyTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalIvyTypography.current

    val shapes: IvyShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalIvyShapes.current
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun IvyTheme(
    theme: Theme,
    design: IvyDesign,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColors: Boolean = false,
    content: @Composable () -> Unit
) {
    val baseColors = design.colors(theme, isDarkTheme)
    val useDynamicColors = dynamicColors && supportsDynamicColor()
    val context = LocalContext.current
    val colors = remember(baseColors, useDynamicColors, context) {
        if (useDynamicColors) {
            baseColors.withDynamicColors(context, trueBlack = theme == Theme.AMOLED_DARK)
        } else {
            baseColors
        }
    }
    val typography = design.typography()
    val shapes = design.shapes()

    CompositionLocalProvider(
        LocalIvyColors provides colors,
        LocalIvyTypography provides typography,
        LocalIvyShapes provides shapes
    ) {
        val view = LocalView.current
        if (!view.isInEditMode && view.context is Activity) {
            SideEffect {
                val window = (view.context as Activity).window
                window.statusBarColor = Color.Transparent.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                    colors.isLight
            }
        }

        IvyMaterial3Theme(
            dark = !colors.isLight,
            isTrueBlack = theme == Theme.AMOLED_DARK,
            dynamicColor = useDynamicColors,
            content = content,
        )
    }
}

/**
 * Material You: keeps Ivy's semantic colors (green/red/orange) and swaps the accent
 * and surfaces for wallpaper-based ones.
 */
@RequiresApi(Build.VERSION_CODES.S)
private fun IvyColors.withDynamicColors(context: Context, trueBlack: Boolean): IvyColors {
    val base = this
    // Legacy components draw white content on `primary`, so always use the
    // light-scheme primary (a darker tone) to keep that contrast in dark mode too.
    val accent = dynamicLightColorScheme(context).primary
    val scheme = if (base.isLight) {
        dynamicLightColorScheme(context)
    } else {
        dynamicDarkColorScheme(context)
    }
    return object : IvyColors by base {
        override val primary = accent
        override val pure = if (trueBlack) base.pure else scheme.surface
        override val medium = scheme.surfaceContainerHigh
    }
}
