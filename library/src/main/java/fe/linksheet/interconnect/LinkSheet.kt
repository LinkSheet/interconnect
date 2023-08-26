package fe.linksheet.interconnect

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * A collection of utilities for interacting with
 * LinkSheet from external clients.
 */
object LinkSheet {
    const val INTERCONNECT_COMPONENT = "fe.linksheet.InterconnectService"

    /**
     * Check if LinkSheet (release, nightly or debug) is installed.
     */
    fun Context.isLinkSheetInstalled(): Boolean {
        return getInstalledPackageName() != null
    }

    /**
     * Get the installed package name, if any.
     * If multiple LinkSheet types (release/nightly/debug) or flavors (pro, foss, legacy) are installed,
     * this will return pro > foss > legacy, each with the build types release > nightly > debug
     * If LinkSheet is not installed, this returns null.
     */
    fun Context.getInstalledPackageName(): String? {
        return LinkSheetPackageName.POSSIBLE_PACKAGE_NAMES.firstOrNull {
            try {
                @Suppress("DEPRECATION")
                packageManager.getApplicationInfo(it, 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }
    }

    fun Context.supportsInterconnect(interconnectComponentName: String = INTERCONNECT_COMPONENT): Boolean {
        val installedPackage = getInstalledPackageName() ?: return false

        return try {
            packageManager.getServiceInfo(
                ComponentName(
                    installedPackage,
                    interconnectComponentName
                ), 0
            )
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * A convenience function for binding the interconnect service.
     *
     * If multiple LinkSheet types (release/nightly/debug) or flavors (pro, foss, legacy) are installed,
     * this will bind to pro > foss > legacy, each with the build types release > nightly > debug
     * If LinkSheet is not installed, this returns null.
     */
    fun Context.bindService(
        interconnectComponentName: String = INTERCONNECT_COMPONENT,
        onBound: (LinkSheetServiceConnection) -> Unit,
    ) {
        if (!isLinkSheetInstalled()) {
            throw IllegalStateException("LinkSheet is not installed!")
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            `package` = getInstalledPackageName()
            component = ComponentName(`package`!!, interconnectComponentName)
        }

        val connection = object : LinkSheetServiceConnection() {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                this.service = ILinkSheetService.Stub.asInterface(service)
                onBound(this)
            }

            override fun disconnect() {
                unbindService(this)
            }
        }

        ContextCompat.startForegroundService(this, intent)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    /**
     * A convenience function for binding the interconnect service.
     *
     * If multiple LinkSheet versions are installed (release/nightly/debug), this will bind to
     * release > nightly > debug
     */
    suspend fun Context.bindService(
        interconnectComponentName: String = INTERCONNECT_COMPONENT,
    ): LinkSheetServiceConnection {
        return suspendCoroutine { continuation ->
            bindService(interconnectComponentName) {
                continuation.resume(it)
            }
        }
    }
}
