package fe.linksheet.interconnect;

import fe.linksheet.interconnect.StringParceledListSlice;

interface ISelectedDomainsCallback {
    void onSelectedDomainsRetrieved(in StringParceledListSlice selectedDomains) = 1;
}