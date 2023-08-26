package fe.linksheet.interconnect

import android.content.Context
import android.content.pm.PackageManager

/**
 * A collection of utilities for interacting with
 * LinkSheet from external clients.
 */
object LinkSheetConnector {
    const val INTERCONNECT_COMPONENT = "fe.linksheet.InterconnectService"

    /**
     * Get a [LinkSheet] instance if LinkSheet is installed
     * If multiple LinkSheet types (release/nightly/debug) or flavors (pro, foss, legacy) are installed,
     * this will return pro > foss > legacy, each with the build types release > nightly > debug
     * If LinkSheet is not installed, this returns null.
     */
    fun Context.getLinkSheet(): LinkSheet? {
        return LinkSheetPackageName.POSSIBLE_PACKAGE_NAMES.firstNotNullOfOrNull {
            try {
                @Suppress("DEPRECATION")
                packageManager.getApplicationInfo(it, 0)
                LinkSheet(it)
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }
        }
    }
}
