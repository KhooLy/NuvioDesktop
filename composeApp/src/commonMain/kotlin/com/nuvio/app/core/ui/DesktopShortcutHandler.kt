package com.nuvio.app.core.ui

import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import com.nuvio.app.AppScreenTab
import com.nuvio.app.isDesktop

internal fun handleDesktopShortcut(
    event: KeyEvent,
    bindings: Map<DesktopShortcutAction, List<DesktopShortcutBinding>>,
    onTabSelected: (AppScreenTab) -> Unit,
    onSearchRequested: () -> Unit,
    onBackRequested: () -> Unit,
): Boolean {
    if (!isDesktop || event.type != KeyEventType.KeyDown) return false

    return when {
        bindings.matches(DesktopShortcutAction.HomeTab, event) -> {
            onTabSelected(AppScreenTab.Home)
            true
        }
        bindings.matches(DesktopShortcutAction.SearchTab, event) -> {
            onTabSelected(AppScreenTab.Search)
            true
        }
        bindings.matches(DesktopShortcutAction.LibraryTab, event) -> {
            onTabSelected(AppScreenTab.Library)
            true
        }
        bindings.matches(DesktopShortcutAction.SettingsTab, event) -> {
            onTabSelected(AppScreenTab.Settings)
            true
        }
        bindings.matches(DesktopShortcutAction.FocusSearch, event) -> {
            onSearchRequested()
            true
        }
        bindings.matches(DesktopShortcutAction.Back, event) -> {
            onBackRequested()
            true
        }
        else -> false
    }
}

private fun Map<DesktopShortcutAction, List<DesktopShortcutBinding>>.matches(
    action: DesktopShortcutAction,
    event: KeyEvent,
): Boolean = this[action].orEmpty().any { it.matches(event) }
