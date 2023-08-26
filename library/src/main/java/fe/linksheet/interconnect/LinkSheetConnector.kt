package fe.linksheet.interconnect

import android.content.ComponentName
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo

/**
 * A utility to check if LinkSheet is installed and supports Interconnect
 */
object LinkSheetConnector {
    const val INTERCONNECT_COMPONENT = "fe.linksheet.InterconnectService"
    const val BASE_PACKAGE_NAME = "fe.linksheet"

    fun getSpecificTypePackageName(flavor: BuildFlavor, type: BuildType): String {
        return apply(apply(BASE_PACKAGE_NAME, flavor.suffix), type.suffix)
    }

    private fun apply(packageName: String, suffix: String?): String {
        return if (suffix != null) "${packageName}.${suffix}" else packageName
    }

    enum class BuildType(val suffix: String? = null) {
        Release, Debug("debug"), Nightly("nightly")
    }

    enum class BuildFlavor(val suffix: String? = null) {
        Pro("pro"), Foss("foss"), Legacy
    }

    val POSSIBLE_PACKAGE_NAMES = BuildFlavor.entries.flatMap { flavor ->
        val flavorPackageName = apply(BASE_PACKAGE_NAME, flavor.suffix)
        BuildType.entries.map { type -> apply(flavorPackageName, type.suffix) }
    }

    /**
     * Get a [LinkSheet] instance if LinkSheet is installed
     * If multiple LinkSheet types (release/nightly/debug) or flavors (pro, foss, legacy) are installed,
     * this will return pro > foss > legacy, each with the build types release > nightly > debug
     * If LinkSheet is not installed, this returns null.
     */
    fun Context.getLinkSheet(interconnectComponent: String = INTERCONNECT_COMPONENT): LinkSheet? {
        return POSSIBLE_PACKAGE_NAMES.firstNotNullOfOrNull {
            getLinkSheet(it, interconnectComponent)
        }
    }

    fun Context.getLinkSheet(
        packageName: String,
        interconnectComponent: String = INTERCONNECT_COMPONENT
    ): LinkSheet? {
        packageManager.getApplicationInfoOrNull(packageName) ?: return null

        val componentName = ComponentName(packageName, interconnectComponent)
        val hasInterconnect = packageManager.getServiceInfoOrNull(componentName)
        return LinkSheet(packageName, if (hasInterconnect != null) componentName else null)
    }

    private fun PackageManager.getApplicationInfoOrNull(packageName: String): ApplicationInfo? {
        return runCatching { getApplicationInfo(packageName, 0) }.getOrNull()
    }

    private fun PackageManager.getServiceInfoOrNull(componentName: ComponentName): ServiceInfo? {
        return runCatching { getServiceInfo(componentName, 0) }.getOrNull()
    }
}
