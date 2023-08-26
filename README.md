# LinkSheetInterConnect
A way to communicate with [LinkSheet](https://github.com/1fexd/LinkSheet/), allowing your app to see which domains LinkSheet will send to it and to request domains be selected.

[![](https://jitpack.io/v/1fexd/LinkSheetInterConnect.svg)](https://jitpack.io/#1fexd/LinkSheetInterConnect)

## Installation
Add JitPack to your Maven repos:
```kotlin
maven(url="https://jitpack.io")
```

Add the dependency:
```kotlin
implementation("com.github.1fexd:LinkSheetInterConnect:<VERSION>")
```

## Usage
The connection is just a basic bound Service using the AIDL system to communicate with clients.

LinkSheetInterconnect provides a few convenience functions and values to reduce boilerplate when handling multiple LinkSheet variants and installation status.

### LinkSheetConnector Object
LinkSheetConnector is the entrypoint to the convenience API.

This object contains some utilities for manually handling the multiple variants of LinkSheet, but the main function is `getLinkSheet()`.

If you just want to get the first available variant of LinkSheet available on the device:
```kotlin
val linkSheet = LinkSheetConnector.getLinkSheet(context)
```

See the comments in LinkSheetConnector for more details on how each variant is prioritized.

If you know the specific variant you need:
```kotlin
val targetPackage = LinkSheetConnector.getSpecificTypePackageName(flavor, type)
val linkSheet = LinkSheetConnector.getLinkSheet(context, targetPackage)
```

### Install Status
If `LinkSheetConnector.getLinkSheet()` returns `null`, no version of LinkSheet was found installed.

In this case, you should prompt the user to download LinkSheet.

### LinkSheet Object
The `LinkSheet` instance returned by `LinkSheetConnector.getLinkSheet()` serves two major functions:

#### Checking for Interconnect Support
To check if the installed LinkSheet supports the interconnect service:
```kotlin
val supportsInterconnect = linkSheet.supportsInterconnect
```

If this returns `false`, then the user has an outdated version of LinkSheet installed and should be prompted to update.

#### Binding to the Service
To bind to the interconnect service:
```kotlin
linkSheet.bindService(context) { connection ->
    // Use the connection here.
}
```

If you're using Kotlin Coroutines, you can use the `suspend` version instead:
```kotlin
val connection = linkSheet.bindService(context)
```

### Using the Connection
`LinkSheet#bindService()` returns an instance of `LinkSheetServiceConnection`.

This is a convenience wrapper both around `ILinkSheetService` (the interconnect interface itself) and `ServiceConnection`.

You can call the interconnect APIs directly on this object and when you finish, simply call `connection.disconnect()`.

## Examples

### Check if LinkSheet is Installed
```kotlin
val linkSheet = LinkSheetConnector.getLinkSheet(context)
val isLinkSheetInstalled = linkSheet != null
```

### Check if LinkSheet Supports Interconnect
```kotlin
val linkSheet = LinkSheetConnector.getLinkSheet(context)
val isInterconnectSupported = linkSheet?.supportsInterconnect == true // If `linkSheet` is null, LinkSheet isn't installed.
```

### Bind to LinkSheet
```kotlin
if (isInterconnectSupported) {
    // `suspend` method of getting the connection.
    val connection = linkSheet.bindService(context)
    
    // Check which domains are selected for your app.
    // Alternative version taking a `callback` argument is also available.
    val selectedDomains = connection.getSelectedDomainsAsync(context.packageName).list
    
    // Request the passed domains be selected for your app.
    val targetComponent: ComponentName = ... // The ComponentName of the component in your app handling the passed links.
    connection.selectDomains(context.packageName, domainsToSelect, targetComponent)
    
    // Disconnect from LinkSheet.
    connection.disconnect()
}
```
