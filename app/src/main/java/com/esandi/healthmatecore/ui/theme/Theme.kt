
package com.esandi.healthmatecore.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = StrongBlue,
    onPrimary = White,
    secondary = Indigo,
    onSecondary = White,
    tertiary = Mint,
    onTertiary = White,
    error = Magenta,
    onError = White,
    background = LightPage,
    onBackground = Color(0xFF1C1C1E),
    surface = White,
    onSurface = Color(0xFF1C1C1E),

    primaryContainer = BlueTintLight,
    onPrimaryContainer = StrongBlue,

    surfaceContainerLowest = White,
    surfaceContainerLow = White,
    surfaceContainer = White,
    surfaceContainerHigh = White,
    surfaceContainerHighest = White
)


private val DarkColorScheme = darkColorScheme(
    primary = BrightBlue,
    onPrimary = Black,
    secondary = SoftViolet,
    onSecondary = Black,
    tertiary = SuccessGreen,
    onTertiary = Black,
    error = Pink,
    onError = Black,
    background = Black,
    onBackground = White,
    surface = DarkCard,
    onSurface = White,

    primaryContainer = BlueTintDark,
    onPrimaryContainer = BrightBlue,

    surfaceContainerLowest = DarkCard,
    surfaceContainerLow = DarkCard,
    surfaceContainer = DarkCard,
    surfaceContainerHigh = DarkCard,
    surfaceContainerHighest = DarkCard
)


@Composable
fun HealthMateCoreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
