package com.nuvio.app.features.settings

import com.nuvio.app.core.ui.DesktopShortcutAction
import com.nuvio.app.core.ui.defaultDesktopShortcutBindings
import com.nuvio.app.core.ui.noDesktopShortcutBinding
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DesktopShortcutSettingsRepositoryTest {
    @Test
    fun playerBindingsJsonUsesBrowserShortcutStrings() {
        val payload = Json.parseToJsonElement(defaultDesktopShortcutBindings.toPlayerBindingsJson()).jsonObject

        val playPauseBindings = payload.getValue(DesktopShortcutAction.PlayPause.id).jsonArray
        val nextEpisodeBindings = payload.getValue(DesktopShortcutAction.NextEpisode.id).jsonArray

        assertTrue(playPauseBindings.all { it.jsonPrimitive.isString })
        assertEquals("Space", playPauseBindings.first().jsonPrimitive.content)
        assertEquals("Shift+KeyN", nextEpisodeBindings.first().jsonPrimitive.content)
    }

    @Test
    fun removedBindingIsSerializedAsEmptyShortcut() {
        val payload = mapOf(
            DesktopShortcutAction.PlayPause to listOf(noDesktopShortcutBinding),
        ).toPlayerBindingsJson()

        val playPauseBindings = Json.parseToJsonElement(payload)
            .jsonObject
            .getValue(DesktopShortcutAction.PlayPause.id)
            .jsonArray

        assertEquals("", playPauseBindings.first().jsonPrimitive.content)
    }
}
