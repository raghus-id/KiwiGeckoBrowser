package com.kiwigeckobrowser.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiwigeckobrowser.app.R
import com.kiwigeckobrowser.app.core.TabManager
import com.kiwigeckobrowser.app.ui.theme.*

/**
 * The main browser screen featuring a bottom address bar in the style of Kiwi Browser.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserScreen(
    tabManager: TabManager,
    onShowTabSwitcher: () -> Unit,
    onShowSettings: () -> Unit
) {
    val activeTabId by tabManager.activeTabId.collectAsState()
    val activeTab = tabManager.getActiveTab()

    // If there is no active tab, just show a blank screen or a full-screen new tab page
    if (activeTab == null) {
        Box(modifier = Modifier.fillMaxSize().background(KiwiBackground))
        return
    }

    val url by activeTab.url.collectAsState()
    val progress by activeTab.progress.collectAsState()
    val canGoBack by activeTab.canGoBack.collectAsState()
    val canGoForward by activeTab.canGoForward.collectAsState()
    val tabs by tabManager.tabs.collectAsState()

    var urlInput by remember(url) { mutableStateOf(url) }
    var isEditingUrl by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KiwiBackground)
            // Kiwi Browser puts everything important at the bottom!
    ) {
        // --- TOP AREA: The Web Page ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // GeckoView rendering area
            GeckoViewComposable(
                tab = activeTab,
                modifier = Modifier.fillMaxSize()
            )

            // Loading Progress Bar at the very top of the screen (or bottom of page, depending on preference)
            if (progress in 0.01f..0.99f) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxWidth().height(3.dp),
                    color = KiwiAccent,
                    trackColor = Color.Transparent
                )
            }
        }

        // --- BOTTOM AREA: The Kiwi-style Toolbar ---
        Surface(
            color = KiwiSurface,
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 4.dp
        ) {
            Column {
                Divider(color = KiwiOutline.copy(alpha = 0.5f), thickness = 1.dp)
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .systemBarsPadding(), // Pad for bottom navigation bar
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    
                    // 1. Home / New Tab button (optional, replacing with Back/Forward if preferred, but Kiwi uses Home often)
                    IconButton(
                        onClick = { activeTab.loadUrl("https://duckduckgo.com") },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Filled.Home, contentDescription = "Home", tint = KiwiOnSurface)
                    }

                    // 2. The Address Bar Capsule
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(KiwiSurfaceVariant),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)
                        ) {
                            // SSL Lock Icon
                            Icon(
                                imageVector = if (url.startsWith("https")) Icons.Filled.Lock else Icons.Filled.Warning,
                                contentDescription = null,
                                tint = if (url.startsWith("https")) KiwiOnSurfaceVariant else Color.Red,
                                modifier = Modifier.size(16.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            // URL Input Field
                            TextField(
                                value = if (isEditingUrl) urlInput else formatUrlForDisplay(url),
                                onValueChange = { urlInput = it },
                                modifier = Modifier.weight(1f),
                                colors = TextFieldDefaults.textFieldColors(
                                    containerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    cursorColor = KiwiAccent,
                                    focusedTextColor = KiwiOnSurface,
                                    unfocusedTextColor = KiwiOnSurface
                                ),
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Uri,
                                    imeAction = ImeAction.Go
                                ),
                                keyboardActions = KeyboardActions(
                                    onGo = {
                                        focusManager.clearFocus()
                                        isEditingUrl = false
                                        val submitUrl = if (!urlInput.contains(".") && !urlInput.contains("://")) {
                                            "https://duckduckgo.com/?q=${android.net.Uri.encode(urlInput)}"
                                        } else if (!urlInput.startsWith("http")) {
                                            "https://$urlInput"
                                        } else {
                                            urlInput
                                        }
                                        activeTab.loadUrl(submitUrl)
                                    }
                                ),
                                placeholder = {
                                    Text(stringResource(R.string.browser_hint), color = KiwiOnSurfaceVariant)
                                }
                            )
                            
                            // Reload / Stop Button
                            IconButton(
                                onClick = { activeTab.reload() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Filled.Refresh, contentDescription = "Reload", tint = KiwiOnSurfaceVariant, modifier = Modifier.size(20.dp))
                            }
                        }
                    }

                    // 3. Tab Switcher Button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = onShowTabSwitcher) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(KiwiSurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${tabs.size}",
                                    color = KiwiOnSurface,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // 4. Main Menu Button
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Menu", tint = KiwiOnSurface)
                        }

                        // Kiwi-style dropdown menu
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(KiwiSurface)
                        ) {
                            DropdownMenuItem(
                                text = { Text("New Tab", color = KiwiOnSurface) },
                                leadingIcon = { Icon(Icons.Filled.Add, null, tint = KiwiOnSurface) },
                                onClick = { showMenu = false; tabManager.createNewTab() }
                            )
                            DropdownMenuItem(
                                text = { Text(if (canGoForward) "Forward" else "Forward (Disabled)", color = if(canGoForward) KiwiOnSurface else KiwiOnSurfaceVariant) },
                                leadingIcon = { Icon(Icons.Filled.ArrowForward, null, tint = if(canGoForward) KiwiOnSurface else KiwiOnSurfaceVariant) },
                                onClick = { showMenu = false; activeTab.goForward() }
                            )
                            Divider(color = KiwiOutline)
                            DropdownMenuItem(
                                text = { Text("Extensions", color = KiwiOnSurface) },
                                leadingIcon = { Icon(Icons.Filled.Extension, null, tint = KiwiOnSurface) },
                                onClick = { showMenu = false; /* TODO navigate to extensions */ }
                            )
                            DropdownMenuItem(
                                text = { Text("Settings", color = KiwiOnSurface) },
                                leadingIcon = { Icon(Icons.Filled.Settings, null, tint = KiwiOnSurface) },
                                onClick = { showMenu = false; onShowSettings() }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Simplifies URLs for display in the address bar (e.g., hiding https:// and trailing slashes).
 */
private fun formatUrlForDisplay(url: String): String {
    return url.replaceFirst(Regex("^https?://"), "")
        .replaceFirst(Regex("^www\\."), "")
        .removeSuffix("/")
}
