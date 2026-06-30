package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = HighDensityPrimary,
    secondary = HighDensitySecondary,
    tertiary = HighDensityTertiary,
    background = HighDensityBackground,
    surface = HighDensitySurface,
    onPrimary = HighDensityOnPrimary,
    onSecondary = HighDensityOnSecondary,
    onTertiary = HighDensityOnTertiary,
    onBackground = HighDensityOnBackground,
    onSurface = HighDensityOnSurface,
    primaryContainer = HighDensityPrimaryContainer,
    onPrimaryContainer = HighDensityOnPrimaryContainer,
    secondaryContainer = HighDensitySecondaryContainer,
    onSecondaryContainer = HighDensityOnSecondaryContainer,
    tertiaryContainer = HighDensityTertiaryContainer,
    onTertiaryContainer = HighDensityOnTertiaryContainer,
    error = HighDensityError,
    onError = HighDensityOnError,
    errorContainer = HighDensityErrorContainer,
    onErrorContainer = HighDensityOnErrorContainer,
    surfaceVariant = HighDensitySurfaceVariant,
    onSurfaceVariant = HighDensityOnSurfaceVariant,
    outline = HighDensityOutline
  )

private val LightColorScheme =
  lightColorScheme(
    primary = HighDensityPrimary,
    secondary = HighDensitySecondary,
    tertiary = HighDensityTertiary,
    background = HighDensityBackground,
    surface = HighDensitySurface,
    onPrimary = HighDensityOnPrimary,
    onSecondary = HighDensityOnSecondary,
    onTertiary = HighDensityOnTertiary,
    onBackground = HighDensityOnBackground,
    onSurface = HighDensityOnSurface,
    primaryContainer = HighDensityPrimaryContainer,
    onPrimaryContainer = HighDensityOnPrimaryContainer,
    secondaryContainer = HighDensitySecondaryContainer,
    onSecondaryContainer = HighDensityOnSecondaryContainer,
    tertiaryContainer = HighDensityTertiaryContainer,
    onTertiaryContainer = HighDensityOnTertiaryContainer,
    error = HighDensityError,
    onError = HighDensityOnError,
    errorContainer = HighDensityErrorContainer,
    onErrorContainer = HighDensityOnErrorContainer,
    surfaceVariant = HighDensitySurfaceVariant,
    onSurfaceVariant = HighDensityOnSurfaceVariant,
    outline = HighDensityOutline
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
