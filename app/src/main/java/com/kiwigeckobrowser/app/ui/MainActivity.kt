package com.kiwigeckobrowser.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kiwigeckobrowser.app.core.GeckoEngine
import com.kiwigeckobrowser.app.core.TabManager
import com.kiwigeckobrowser.app.ui.theme.KiwiGeckoBrowserTheme

/**
 * The main entry point for the KiwiGeckoBrowser.
 */
class MainActivity : ComponentActivity() {

    private val tabManager = TabManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. Initialize the massive GeckoRuntime in the background
        GeckoEngine.init(this)

        // 2. Open an initial tab
        if (tabManager.tabs.value.isEmpty()) {
            tabManager.createNewTab("https://start.duckduckgo.com/")
        }

        setContent {
            KiwiGeckoBrowserTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Browser) }
                
                // Handle system back button behavior beautifully
                BackHandler(enabled = true) {
                    when (currentScreen) {
                        is Screen.TabSwitcher -> {
                            currentScreen = Screen.Browser
                        }
                        is Screen.Settings -> {
                            currentScreen = Screen.Browser
                        }
                        is Screen.Browser -> {
                            val activeTab = tabManager.getActiveTab()
                            if (activeTab != null && activeTab.canGoBack.value) {
                                // Navigate the current web page back
                                activeTab.goBack()
                            } else {
                                // If the web page can't go back, and we have multiple tabs, 
                                // maybe go to Tab Switcher instead of exiting the app natively
                                if (tabManager.tabs.value.size > 1) {
                                    currentScreen = Screen.TabSwitcher
                                } else {
                                    // Let Android exit the app
                                    finish()
                                }
                            }
                        }
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        is Screen.Browser -> {
                            BrowserScreen(
                                tabManager = tabManager,
                                onShowTabSwitcher = { currentScreen = Screen.TabSwitcher },
                                onShowSettings = { currentScreen = Screen.Settings }
                            )
                        }
                        is Screen.TabSwitcher -> {
                            TabSwitcher(
                                tabManager = tabManager,
                                onClose = { currentScreen = Screen.Browser }
                            )
                        }
                        is Screen.Settings -> {
                            // Placeholder for settings screen
                            SettingsScreen(onBack = { currentScreen = Screen.Browser })
                        }
                    }
                }
            }
        }
    }
}

sealed class Screen {
    object Browser : Screen()
    object TabSwitcher : Screen()
    object Settings : Screen()
}
