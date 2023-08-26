package fe.linksheet.interconnect

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LinkSheet(
    val packageName: String,
    val interconnectComponentName: ComponentName?,
) {
    val supportsInterconnect = interconnectComponentName != null

    /**
     * A convenience function for binding the Interconnect service.
     *
     * If multiple LinkSheet types (release/nightly/debug) or flavors (pro, foss, legacy) are installed,
     * this will bind to pro > foss > legacy, each with the build types release > nightly > debug
     * If LinkSheet is not installed, this returns null.
     */
    fun bindService(context: Context, onBound: (LinkSheetServiceConnection) -> Unit) {
        with (context) {
            if (!supportsInterconnect) {
                throw IllegalStateException("Installed LinkSheet version does not support Interconnect")
            }

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
    }


    /**
     * A convenience function for binding the Interconnect service.
     *
     * If multiple LinkSheet types (release/nightly/debug) or flavors (pro, foss, legacy) are installed,
     * this will bind to pro > foss > legacy, each with the build types release > nightly > debug
     */
    suspend fun bindService(context: Context): LinkSheetServiceConnection {
        return suspendCoroutine { continuation ->
            bindService(context) { continuation.resume(it) }
        }
    }
}