package com.nuvio.app.features.settings

internal expect object DesktopShortcutStorage {
    fun loadBindings(): String?
    fun saveBindings(payload: String)
}
