package fe.linksheet.interconnect;

import fe.linksheet.interconnect.StringParceledListSlice;

interface IDomainSelectionResultCallback {
    void onDomainSelectionConfirmed() = 1;
    void onDomainSelectionCancelled() = 2;
    void onSomeDomainsSelected(in StringParceledListSlice selectedDomains) = 3;
}