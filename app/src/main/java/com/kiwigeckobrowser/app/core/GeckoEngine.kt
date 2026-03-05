package com.kiwigeckobrowser.app.core

import android.content.Context
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoRuntimeSettings
import org.mozilla.geckoview.WebExtensionController

/**
 * Singleton to manage the GeckoRuntime.
 * There should only be one GeckoRuntime per application.
 */
object GeckoEngine {
    private var runtime: GeckoRuntime? = null

    /**
     * Initializes the GeckoRuntime if it hasn't been already.
     */
    fun init(context: Context): GeckoRuntime {
        if (runtime == null) {
            val settings = GeckoRuntimeSettings.Builder()
                .javaScriptEnabled(true)
                // Enable extensions support
                .build()

            runtime = GeckoRuntime.create(context.applicationContext, settings)
        }
        return runtime!!
    }

    /**
     * Returns the initialized GeckoRuntime. Throws if not initialized.
     */
    fun getRuntime(): GeckoRuntime {
        return runtime ?: throw IllegalStateException("GeckoEngine not initialized! Call init(context) first.")
    }

    /**
     * Convenience method to get the WebExtensionController for installing extensions.
     */
    fun getExtensionController(): WebExtensionController {
        return getRuntime().webExtensionController
    }
}
