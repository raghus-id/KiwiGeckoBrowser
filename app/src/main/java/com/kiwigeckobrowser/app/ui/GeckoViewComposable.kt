package com.kiwigeckobrowser.app.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.kiwigeckobrowser.app.core.BrowserTab
import org.mozilla.geckoview.GeckoView

/**
 * A Jetpack Compose wrapper for Mozilla's GeckoView.
 * This binds a running GeckoSession to the Android View layer.
 */
@Composable
fun GeckoViewComposable(
    tab: BrowserTab,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // We remember the GeckoView instance so it isn't recreated unnecessarily during recompositions
    val geckoView = remember {
        GeckoView(context).apply {
            // Ensures the surface is fully transparent/black until the page renders, preventing white flashes
            // ... configure surface layer if needed
        }
    }

    // Attach and detach the session based on the Composable's lifecycle
    DisposableEffect(tab.id) {
        // Bind the active session to the view
        geckoView.setSession(tab.session)

        onDispose {
            // Unbind when this composable leaves the screen (e.g., switching tabs)
            // It's safe to pass null to unbind
            geckoView.releaseSession()
        }
    }

    AndroidView(
        factory = { geckoView },
        modifier = modifier.fillMaxSize(),
        update = { view ->
            // Re-bind just to be absolutely sure we're attached in case of AndroidView updates
            if (view.session != tab.session) {
                view.setSession(tab.session)
            }
        }
    )
}
