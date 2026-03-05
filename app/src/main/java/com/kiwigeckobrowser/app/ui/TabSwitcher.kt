package com.kiwigeckobrowser.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiwigeckobrowser.app.core.BrowserTab
import com.kiwigeckobrowser.app.core.TabManager
import com.kiwigeckobrowser.app.ui.theme.*

/**
 * A grid-based tab switcher mirroring modern mobile browsers.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabSwitcher(
    tabManager: TabManager,
    onClose: () -> Unit
) {
    val tabs by tabManager.tabs.collectAsState()
    val activeTabId by tabManager.activeTabId.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Open Tabs (${tabs.size})", color = KiwiOnSurface) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = KiwiOnSurface)
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        tabManager.createNewTab()
                        onClose()
                    }) {
                        Icon(Icons.Filled.Add, contentDescription = "New Tab", tint = KiwiOnSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = KiwiBackground)
            )
        },
        bottomBar = {
            // A bottom bar for "New Tab" specifically to keep it reachable
            Surface(
                color = KiwiSurface,
                modifier = Modifier.fillMaxWidth().systemBarsPadding()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { 
                            tabManager.createNewTab()
                            onClose()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = KiwiAccent),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("New tab", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        containerColor = KiwiBackground
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(paddingValues).fillMaxSize()
        ) {
            items(tabs, key = { it.id }) { tab ->
                val isActive = tab.id == activeTabId
                TabCard(
                    tab = tab,
                    isActive = isActive,
                    onClick = {
                        tabManager.switchToTab(tab.id)
                        onClose()
                    },
                    onCloseClick = {
                        tabManager.closeTab(tab.id)
                        if (tabs.size == 1) { // We just closed the last tab
                            tabManager.createNewTab() // Make sure at least one exists
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun TabCard(
    tab: BrowserTab,
    isActive: Boolean,
    onClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    val title by tab.title.collectAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = KiwiSurfaceVariant
        ),
        border = if (isActive) androidx.compose.foundation.BorderStroke(2.dp, KiwiAccent) else null
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Tab Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(KiwiSurface)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title.ifEmpty { "New Tab" },
                    color = KiwiOnSurface,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onCloseClick,
                    modifier = Modifier.size(20.dp).padding(start = 4.dp)
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close Tab", tint = KiwiOnSurfaceVariant, modifier = Modifier.size(16.dp))
                }
            }
            
            // Tab Preview (For now, just a placeholder as capturing actual bitmaps from Gecko is complex)
            Box(
                modifier = Modifier.fillMaxSize().background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                // We'd render a bitmap snapshot of the GeckoSession here ideally
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.WebAsset,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.LightGray
                )
            }
        }
    }
}
