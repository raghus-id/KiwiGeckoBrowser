package com.kiwigeckobrowser.app.core

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Manages the collection of open BrowserTabs and their states.
 */
class TabManager {

    private val _tabs = MutableStateFlow<List<BrowserTab>>(emptyList())
    val tabs: StateFlow<List<BrowserTab>> = _tabs.asStateFlow()

    private val _activeTabId = MutableStateFlow<String?>(null)
    val activeTabId: StateFlow<String?> = _activeTabId.asStateFlow()

    /**
     * Gets the currently active tab object.
     */
    fun getActiveTab(): BrowserTab? {
        val id = _activeTabId.value ?: return null
        return _tabs.value.find { it.id == id }
    }

    /**
     * Creates a new tab, opens its GeckoSession, and switches to it.
     */
    fun createNewTab(url: String = "https://duckduckgo.com/") {
        val newTab = BrowserTab(url)
        
        // Open the GeckoSession immediately
        newTab.open()
        
        _tabs.update { it + newTab }
        _activeTabId.value = newTab.id
    }

    /**
     * Closes a specific tab entirely.
     */
    fun closeTab(tabId: String) {
        val tabToClose = _tabs.value.find { it.id == tabId }
        if (tabToClose != null) {
            // Unbind and destroy the GeckoSession
            tabToClose.close()
            
            _tabs.update { currentTabs ->
                val newTabs = currentTabs.filter { it.id != tabId }
                
                // If we closed the active tab, switch to the last remaining one (if any)
                if (_activeTabId.value == tabId) {
                    _activeTabId.value = newTabs.lastOrNull()?.id
                }
                
                newTabs
            }
        }
    }

    /**
     * Switches the active focus to a different tab.
     */
    fun switchToTab(tabId: String) {
        val exists = _tabs.value.any { it.id == tabId }
        if (exists) {
            _activeTabId.value = tabId
        }
    }
}
