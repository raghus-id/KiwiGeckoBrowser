package com.kiwigeckobrowser.app.core

import org.mozilla.geckoview.GeckoSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * Wrapper for a single browser tab using GeckoSession.
 */
class BrowserTab(private val initialUrl: String = "https://duckduckgo.com/") {
    val id: String = UUID.randomUUID().toString()
    
    // The underlying GeckoView session driving this tab
    val session: GeckoSession = GeckoSession()

    private val _url = MutableStateFlow(initialUrl)
    val url: StateFlow<String> = _url.asStateFlow()

    private val _title = MutableStateFlow("New Tab")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    private val _canGoForward = MutableStateFlow(false)
    val canGoForward: StateFlow<Boolean> = _canGoForward.asStateFlow()

    init {
        // Set up the session listeners
        session.navigationDelegate = object : GeckoSession.NavigationDelegate {
            override fun onLocationChange(p0: GeckoSession, p1: String?, p2: MutableList<GeckoSession.PermissionDelegate.ContentPermission>?) {
                p1?.let { _url.value = it }
            }

            override fun onCanGoBack(p0: GeckoSession, p1: Boolean) {
                _canGoBack.value = p1
            }

            override fun onCanGoForward(p0: GeckoSession, p1: Boolean) {
                _canGoForward.value = p1
            }
        }

        session.progressDelegate = object : GeckoSession.ProgressDelegate {
            override fun onPageStart(p0: GeckoSession, p1: String) {
                _progress.value = 0.1f
            }

            override fun onProgressChange(p0: GeckoSession, p1: Int) {
                _progress.value = p1 / 100f
            }

            override fun onPageStop(p0: GeckoSession, p1: Boolean) {
                _progress.value = 1.0f
            }

            override fun onSecurityChange(p0: GeckoSession, p1: GeckoSession.ProgressDelegate.SecurityInformation) {
                // Handle security info changes if needed
            }
        }

        session.contentDelegate = object : GeckoSession.ContentDelegate {
            override fun onTitleChange(p0: GeckoSession, p1: String?) {
                p1?.let { _title.value = it }
            }
            
            override fun onCloseRequest(p0: GeckoSession) {
                // Handle tab close request from window.close()
            }
        }
    }

    /**
     * Opens a new session and attaches it to the GeckoRuntime.
     */
    fun open() {
        if (!session.isOpen) {
            session.open(GeckoEngine.getRuntime())
            session.loadUri(initialUrl)
        }
    }

    /**
     * Closes the session to free up memory.
     */
    fun close() {
        if (session.isOpen) {
            session.close()
        }
    }

    fun loadUrl(newUrl: String) {
        session.loadUri(newUrl)
    }

    fun goBack() {
        if (_canGoBack.value) session.goBack()
    }

    fun goForward() {
        if (_canGoForward.value) session.goForward()
    }
    
    fun reload() {
        session.reload()
    }
}
