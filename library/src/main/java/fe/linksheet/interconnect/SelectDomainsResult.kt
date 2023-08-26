package fe.linksheet.interconnect

sealed class SelectDomainsResult {
    data object ResultCanceled : SelectDomainsResult()
    data object ResultConfirmed : SelectDomainsResult()
    data class ResultPartial(val selectedDomains: List<String>) : SelectDomainsResult()
}
