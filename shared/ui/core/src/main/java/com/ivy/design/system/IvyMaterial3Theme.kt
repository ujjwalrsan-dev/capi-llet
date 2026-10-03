package com.ivy.design.system

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.ivy.design.system.colors.IvyColors

@Composable
fun IvyMaterial3Theme(
    isTrueBlack: Boolean,
    dark: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (dynamicColor && supportsDynamicColor()) {
        val context = LocalContext.current
        if (dark) {
            dynamicDarkColorScheme(context).let { scheme ->
                if (isTrueBlack) {
                    scheme.copy(background = IvyColors.TrueBlack, surface = IvyColors.TrueBlack)
                } else {
                    scheme
                }
            }
        } else {
            dynamicLightColorScheme(context)
        }
    } else if (dark) {
        ivyDarkColorScheme(isTrueBlack)
    } else {
        ivyLightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}

/** Material You dynamic color is available on Android 12 (API 31) and above. */
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
fun supportsDynamicColor(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

private fun ivyLightColorScheme(): ColorScheme = ColorScheme(
    primary = IvyColors.Purple.primary,
    onPrimary = IvyColors.White,
    primaryContainer = IvyColors.Purple.light,
    onPrimaryContainer = IvyColors.White,
    inversePrimary = IvyColors.Purple.dark,
    secondary = IvyColors.Green.primary,
    onSecondary = IvyColors.White,
    secondaryContainer = IvyColors.Green.light,
    onSecondaryContainer = IvyColors.White,
    tertiary = IvyColors.Green.primary,
    onTertiary = IvyColors.White,
    tertiaryContainer = IvyColors.Green.light,
    onTertiaryContainer = IvyColors.White,

    error = IvyColors.Red.primary,
    onError = IvyColors.White,
    errorContainer = IvyColors.Red.light,
    onErrorContainer = IvyColors.White,

    background = IvyColors.White,
    onBackground = IvyColors.Black,
    surface = IvyColors.White,
    onSurface = IvyColors.Black,
    surfaceVariant = IvyColors.ExtraLightGray,
    onSurfaceVariant = IvyColors.Black,
    surfaceTint = IvyColors.Black,
    inverseSurface = IvyColors.DarkGray,
    inverseOnSurface = IvyColors.White,

    outline = IvyColors.Gray,
    outlineVariant = IvyColors.DarkGray,
    scrim = IvyColors.ExtraDarkGray.copy(alpha = 0.8f)
)

private fun ivyDarkColorScheme(isTrueBlack: Boolean): ColorScheme = ColorScheme(
    primary = IvyColors.Purple.primary,
    onPrimary = IvyColors.White,
    primaryContainer = IvyColors.Purple.light,
    onPrimaryContainer = IvyColors.White,
    inversePrimary = IvyColors.Purple.dark,
    secondary = IvyColors.Green.primary,
    onSecondary = IvyColors.White,
    secondaryContainer = IvyColors.Green.light,
    onSecondaryContainer = IvyColors.White,
    tertiary = IvyColors.Green.primary,
    onTertiary = IvyColors.White,
    tertiaryContainer = IvyColors.Green.light,
    onTertiaryContainer = IvyColors.White,

    error = IvyColors.Red.primary,
    onError = IvyColors.White,
    errorContainer = IvyColors.Red.light,
    onErrorContainer = IvyColors.White,

    background = if (isTrueBlack) IvyColors.TrueBlack else IvyColors.Black,
    onBackground = IvyColors.White,
    surface = if (isTrueBlack) IvyColors.TrueBlack else IvyColors.Black,
    onSurface = IvyColors.White,
    surfaceVariant = IvyColors.ExtraDarkGray,
    onSurfaceVariant = IvyColors.White,
    surfaceTint = IvyColors.White,
    inverseSurface = IvyColors.LightGray,
    inverseOnSurface = if (isTrueBlack) IvyColors.TrueBlack else IvyColors.Black,

    outline = IvyColors.Gray,
    outlineVariant = IvyColors.LightGray,
    scrim = IvyColors.ExtraLightGray.copy(alpha = 0.8f)
)
