package com.artemonre.hireme.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.artemonre.hireme.portfolio.PortfolioScreen
import com.artemonre.hireme.theme.HireMeTheme
import com.artemonre.hireme.theme.ThemeMode

@Composable
fun AppNavigation(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
) {
    val backStack = remember { mutableStateListOf<Route>(PortfolioRoute) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<PortfolioRoute> {
                PortfolioScreen(
                    themeMode = themeMode,
                    onThemeModeChange = onThemeModeChange,
                )
            }
        },
    )
}

@Composable
@Preview
private fun AppNavigationPreview() {
    var themeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }
    HireMeTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppNavigation(
                themeMode = themeMode,
                onThemeModeChange = { themeMode = it },
            )
        }
    }
}
