package com.nuvio.app.features.settings

import com.nuvio.app.core.ui.DesktopShortcutAction
import com.nuvio.app.core.ui.DesktopShortcutBinding
import com.nuvio.app.core.ui.defaultDesktopShortcutBindings
import com.nuvio.app.core.ui.noDesktopShortcutBinding
import com.nuvio.app.isDesktop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put

private val playerShortcutJson = Json { encodeDefaults = true }

internal data class DesktopShortcutSettingsUiState(
    val bindings: Map<DesktopShortcutAction, List<DesktopShortcutBinding>> = defaultDesktopShortcutBindings,
)

internal object DesktopShortcutSettingsRepository {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    private val _uiState = MutableStateFlow(DesktopShortcutSettingsUiState())
    val uiState: StateFlow<DesktopShortcutSettingsUiState> = _uiState.asStateFlow()

    private var hasLoaded = false

    fun ensureLoaded() {
        if (!isDesktop) return
        if (hasLoaded) return
        hasLoaded = true
        val stored = DesktopShortcutStorage.loadBindings()
            ?.let { payload -> runCatching { json.decodeFromString<StoredShortcutBindings>(payload) }.getOrNull() }
            ?.bindings
            .orEmpty()
        _uiState.value = DesktopShortcutSettingsUiState(mergeWithDefaults(stored))
    }

    fun onProfileChanged() {
        if (!isDesktop) return
        hasLoaded = false
        ensureLoaded()
    }

    fun clearLocalState() {
        if (!isDesktop) return
        hasLoaded = false
        _uiState.value = DesktopShortcutSettingsUiState()
    }

    fun setBinding(
        action: DesktopShortcutAction,
        slot: Int,
        binding: DesktopShortcutBinding,
    ): Boolean {
        ensureLoaded()
        if (binding in _uiState.value.bindings
                .filterKeys { it != action }
                .values
                .flatten()
        ) {
            return false
        }
        val current = _uiState.value.bindings[action].orEmpty().toMutableList()
        if (slot !in current.indices) return false
        current[slot] = binding
        update(action, current)
        return true
    }

    fun removeBinding(action: DesktopShortcutAction, slot: Int): Boolean {
        ensureLoaded()
        val current = _uiState.value.bindings[action].orEmpty().toMutableList()
        if (slot !in current.indices) return false
        current[slot] = noDesktopShortcutBinding
        update(action, current)
        return true
    }

    fun resetAction(action: DesktopShortcutAction) {
        ensureLoaded()
        update(action, defaultDesktopShortcutBindings[action].orEmpty())
    }

    fun resetAll() {
        ensureLoaded()
        _uiState.value = DesktopShortcutSettingsUiState(defaultDesktopShortcutBindings)
        persist()
    }

    fun playerBindingsJson(): String {
        ensureLoaded()
        return _uiState.value.bindings.toPlayerBindingsJson()
    }

    private fun update(action: DesktopShortcutAction, bindings: List<DesktopShortcutBinding>) {
        _uiState.value = _uiState.value.copy(
            bindings = _uiState.value.bindings + (action to bindings),
        )
        persist()
    }

    private fun persist() {
        DesktopShortcutStorage.saveBindings(
            json.encodeToString(
                StoredShortcutBindings(
                    bindings = _uiState.value.bindings.mapKeys { it.key.id },
                ),
            ),
        )
    }

    private fun mergeWithDefaults(
        stored: Map<String, List<DesktopShortcutBinding>>,
    ): Map<DesktopShortcutAction, List<DesktopShortcutBinding>> =
        defaultDesktopShortcutBindings.mapValues { (action, defaults) ->
            defaults.mapIndexed { index, defaultBinding ->
                stored[action.id]
                    ?.getOrNull(index)
                    ?: defaultBinding
            }
        }

    @Serializable
    private data class StoredShortcutBindings(
        val bindings: Map<String, List<DesktopShortcutBinding>> = emptyMap(),
    )
}

internal fun Map<DesktopShortcutAction, List<DesktopShortcutBinding>>.toPlayerBindingsJson(): String =
    buildJsonObject {
        forEach { (action, bindings) ->
            put(action.id, playerShortcutJson.encodeToJsonElement(bindings.map(DesktopShortcutBinding::storageValue)))
        }
    }.toString()
