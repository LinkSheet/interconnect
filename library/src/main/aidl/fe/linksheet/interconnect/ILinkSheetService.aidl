package fe.linksheet.interconnect;

import android.content.ComponentName;
import fe.linksheet.interconnect.ISelectedDomainsCallback;
import fe.linksheet.interconnect.StringParceledListSlice;

interface ILinkSheetService {
    // Retrieve the user-selected domains for the passed package.
    // Currently, the passed package must match the calling package.
    // DEPRECATED: Use the version with the callback instead.
    StringParceledListSlice getSelectedDomains(String packageName) = 1;

    void getSelectedDomainsAsync(String packageName, in ISelectedDomainsCallback callback) = 3;

    // Request that the passed domains be set as preferred for the
    // passed package.
    // The ComponentName should point to the Activity handling the domains.
    // Currently, the passed package must match the calling package.
    void selectDomains(
        String packageName,
        in StringParceledListSlice domains,
        in ComponentName componentName
    ) = 2;
}