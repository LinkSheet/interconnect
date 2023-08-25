# LinkSheetInterconnect
A way to communicate with [LinkSheet](https://github.com/1fexd/LinkSheet/), allowing your app to see which domains LinkSheet will send to it and to request domains be selected.

[![GitHub Release](https://img.shields.io/github/v/release/1fexd/LinkSheetInterConnect?style=for-the-badge&logo=github&label=Version&color=orange)](https://github.com/1fexd/LinkSheetInterConnect/releases)

## Installation
Add JitPack to your Maven repos:

```groovy
maven { url 'https://jitpack.io' }
```

Add the dependency:

```groovy
implementation 'com.github.1fexd:LinkSheetInterConnect:<VERSION>'
```

## Usage
The `LinkSheet` object has most of the needed utilities for connecting to LinkSheet.

### Installation Status
To check if LinkSheet is installed, use `isLinkSheetInstalled()`:

```kotlin
val linkSheetInstalled = with (LinkSheet) {
    context.isLinkSheetInstalled()
}
```

### Interconnect Support
To check if the installed LinkSheet supports the interconnect service, use `supportsInterconnect()`:

```kotlin
val supportsInterconnect = with (LinkSheet) {
    context.supportsInterconnect()
}
```

This function will also return false if LinkSheet isn't installed.

### Bind to LinkSheet
To bind to the LinkSheet service, use `bindService()`.

In Kotlin, you can use the `suspend` version of the function in a Coroutine scope to avoid using callbacks:

```kotlin
val connection = with (LinkSheet) {
    context.bindService()
}
```

In Java, or if you don't have a suspend context available, you can use the callback version:

```kotlin
with (LinkSheet) {
    context.bindService { connection ->
        // Use the connection here.
    }
}
```

### Using the Connection
When you bind to LinkSheet with the `bindService()` function, a `LinkSheetServiceConnection` is provided to you.

This implements all functions LinkSheet supports and adds a `disconnect()` function that should be called when you're done communicating with LinkSheet.

`LinkSheetServiceConnection` also implements `ServiceConnection`. The `disconnect()` function is just a convenient way to unbind LinkSheet.
