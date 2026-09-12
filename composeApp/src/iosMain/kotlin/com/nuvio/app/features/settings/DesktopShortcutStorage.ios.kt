package com.nuvio.app.features.settings

internal actual object DesktopShortcutStorage {
    actual fun loadBindings(): String? = null

    actual fun saveBindings(payload: String) = Unit
}
