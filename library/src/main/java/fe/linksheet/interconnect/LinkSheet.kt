package fe.linksheet.interconnect

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LinkSheet(
    private val packageName: String,
    interconnectComponentClassName: String = LinkSheetConnector.INTERCONNECT_COMPONENT
) {
    private val interconnectComponentName = ComponentName(
        packageName, interconnectComponentClassName
    )

    fun Context.supportsInterconnect(): Boolean {
        return try {
            packageManager.getServiceInfo(interconnectComponentName, 0)
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
    fun Context.bindService(onBound: (LinkSheetServiceConnection) -> Unit) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            `package` = this@LinkSheet.packageName
            component = interconnectComponentName
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
     * If multiple LinkSheet types (release/nightly/debug) or flavors (pro, foss, legacy) are installed,
     * this will bind to pro > foss > legacy, each with the build types release > nightly > debug
     */
    suspend fun Context.bindService(): LinkSheetServiceConnection {
        return suspendCoroutine { continuation ->
            bindService { continuation.resume(it) }
        }
    }
}