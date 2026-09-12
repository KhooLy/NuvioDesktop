package com.nuvio.app.features.settings

import com.nuvio.app.core.storage.DesktopStorage
import com.nuvio.app.core.storage.ProfileScopedKey

internal actual object DesktopShortcutStorage {
    private const val bindingsKey = "bindings"
    private val store = DesktopStorage.store("nuvio_desktop_shortcuts")

    actual fun loadBindings(): String? =
        store.getString(ProfileScopedKey.of(bindingsKey))

    actual fun saveBindings(payload: String) {
        store.putString(ProfileScopedKey.of(bindingsKey), payload)
    }
}
