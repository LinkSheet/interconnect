package fe.linksheet.interconnect

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Wraps the connection and binder for the interconnect service,
 * and provides a convenience function for the client to disconnect.
 */
abstract class LinkSheetServiceConnection : ServiceConnection, ILinkSheetService {
    internal var service: ILinkSheetService? = null

    /**
     * Clients should call this when they're done
     * using the service. Once this is called,
     * the methods in this class are no longer usable.
     */
    abstract fun disconnect()

    final override fun onServiceDisconnected(name: ComponentName?) {
        service = null
    }

    @Deprecated(
        "Deprecated.",
        replaceWith = ReplaceWith(
            expression = "getSelectedDomainsAsync(packageName, callback)",
            imports = arrayOf("fe.linksheet.interconnect.ISelectedDomainsCallback.Stub"),
        ),
    )
    final override fun getSelectedDomains(packageName: String): StringParceledListSlice {
        assertService()
        return service!!.getSelectedDomains(packageName)
    }

    suspend fun getSelectedDomainsAsync(packageName: String): StringParceledListSlice {
        assertService()
        return suspendCoroutine { continuation ->
            getSelectedDomainsAsync(packageName, object : ISelectedDomainsCallback.Stub() {
                override fun onSelectedDomainsRetrieved(selectedDomains: StringParceledListSlice) {
                    continuation.resume(selectedDomains)
                }
            })
        }
    }

    final override fun getSelectedDomainsAsync(
        packageName: String,
        callback: ISelectedDomainsCallback,
    ) {
        assertService()
        service!!.getSelectedDomainsAsync(packageName, callback)
    }

    final override fun selectDomains(
        packageName: String,
        domains: StringParceledListSlice,
        componentName: ComponentName,
    ) {
        assertService()
        service?.selectDomains(packageName, domains, componentName)
    }

    fun selectDomains(
        packageName: String,
        domains: List<String>,
        componentName: ComponentName,
    ) {
        selectDomains(
            packageName,
            StringParceledListSlice(domains),
            componentName
        )
    }

    final override fun selectDomainsWithCallback(
        packageName: String,
        domains: StringParceledListSlice,
        componentName: ComponentName,
        callback: IDomainSelectionResultCallback,
    ) {
        assertService()
        service!!.selectDomainsWithCallback(
            packageName, domains, componentName, callback,
        )
    }

    fun selectDomainsWithCallback(
        packageName: String,
        domains: List<String>,
        componentName: ComponentName,
        callback: IDomainSelectionResultCallback,
    ) {
        selectDomainsWithCallback(
            packageName,
            StringParceledListSlice(domains),
            componentName,
            callback,
        )
    }

    suspend fun selectDomainsWithResult(
        packageName: String,
        domains: List<String>,
        componentName: ComponentName,
    ): SelectDomainsResult {
        assertService()

        return suspendCoroutine { continuation ->
            selectDomainsWithCallback(
                packageName, domains, componentName,
                object : IDomainSelectionResultCallback.Stub() {
                    override fun onDomainSelectionConfirmed() {
                        continuation.resume(SelectDomainsResult.ResultConfirmed)
                    }

                    override fun onDomainSelectionCancelled() {
                        continuation.resume(SelectDomainsResult.ResultCanceled)
                    }

                    override fun onSomeDomainsSelected(selectedDomains: StringParceledListSlice?) {
                        continuation.resume(SelectDomainsResult.ResultPartial(selectedDomains?.list ?: listOf()))
                    }
                }
            )
        }
    }

    final override fun asBinder(): IBinder {
        assertService()
        return service!!.asBinder()
    }

    private fun assertService() {
        if (service == null) {
            throw IllegalStateException("Service not bound!")
        }
    }
}
