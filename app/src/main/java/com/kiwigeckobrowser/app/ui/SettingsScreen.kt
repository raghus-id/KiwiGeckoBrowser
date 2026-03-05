package com.kiwigeckobrowser.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kiwigeckobrowser.app.ui.theme.KiwiBackground
import com.kiwigeckobrowser.app.ui.theme.KiwiOnSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KiwiBackground)
            .systemBarsPadding()
    ) {
        TopAppBar(
            title = { Text("Settings", fontWeight = FontWeight.Bold, color = KiwiOnSurface) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = KiwiOnSurface)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = KiwiBackground)
        )
        
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Settings Page Coming Soon\n(Once WebExtensions are integrated)", color = KiwiOnSurface)
        }
    }
}
