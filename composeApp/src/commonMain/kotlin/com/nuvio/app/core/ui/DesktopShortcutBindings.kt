package com.nuvio.app.core.ui

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import kotlinx.serialization.Serializable

internal enum class DesktopShortcutSection {
    General,
    Player,
}

internal enum class DesktopShortcutAction(
    val id: String,
    val section: DesktopShortcutSection,
) {
    HomeTab("homeTab", DesktopShortcutSection.General),
    SearchTab("searchTab", DesktopShortcutSection.General),
    LibraryTab("libraryTab", DesktopShortcutSection.General),
    SettingsTab("settingsTab", DesktopShortcutSection.General),
    FocusSearch("focusSearch", DesktopShortcutSection.General),
    Back("back", DesktopShortcutSection.General),
    PlayPause("playPause", DesktopShortcutSection.Player),
    SubtitleToggle("subtitleToggle", DesktopShortcutSection.Player),
    Mute("mute", DesktopShortcutSection.Player),
    SeekBack("seekBack", DesktopShortcutSection.Player),
    SeekForward("seekForward", DesktopShortcutSection.Player),
    VolumeUp("volumeUp", DesktopShortcutSection.Player),
    VolumeDown("volumeDown", DesktopShortcutSection.Player),
    Fullscreen("fullscreen", DesktopShortcutSection.Player),
    SkipIntro("skipIntro", DesktopShortcutSection.Player),
    SubtitleSelector("subtitleSelector", DesktopShortcutSection.Player),
    SourceList("sourceList", DesktopShortcutSection.Player),
    EpisodeList("episodeList", DesktopShortcutSection.Player),
    NextEpisode("nextEpisode", DesktopShortcutSection.Player),
}

@Serializable
internal data class DesktopShortcutBinding(
    val code: String,
    val shift: Boolean = false,
    val ctrl: Boolean = false,
    val alt: Boolean = false,
    val meta: Boolean = false,
) {
    fun storageValue(): String = buildString {
        if (ctrl) append("Ctrl+")
        if (alt) append("Alt+")
        if (shift) append("Shift+")
        if (meta) append("Meta+")
        append(code)
    }

    fun displayValue(): String = buildString {
        if (ctrl) append("Ctrl + ")
        if (alt) append("Alt + ")
        if (shift) append("Shift + ")
        if (meta) append("Meta + ")
        append(code.displayName())
    }
}

internal val noDesktopShortcutBinding = DesktopShortcutBinding(code = "")

internal val DesktopShortcutBinding.isDisabled: Boolean
    get() = code.isBlank()

internal val defaultDesktopShortcutBindings: Map<DesktopShortcutAction, List<DesktopShortcutBinding>> = mapOf(
    DesktopShortcutAction.HomeTab to listOf(binding("Digit1"), binding("Numpad1")),
    DesktopShortcutAction.SearchTab to listOf(binding("Digit2"), binding("Numpad2")),
    DesktopShortcutAction.LibraryTab to listOf(binding("Digit3"), binding("Numpad3")),
    DesktopShortcutAction.SettingsTab to listOf(binding("Digit4"), binding("Numpad4")),
    DesktopShortcutAction.FocusSearch to listOf(binding("Slash"), binding("Digit0"), binding("Numpad0")),
    DesktopShortcutAction.Back to listOf(binding("Escape")),
    DesktopShortcutAction.PlayPause to listOf(binding("Space"), binding("KeyK")),
    DesktopShortcutAction.SubtitleToggle to listOf(binding("KeyC")),
    DesktopShortcutAction.Mute to listOf(binding("KeyM")),
    DesktopShortcutAction.SeekBack to listOf(binding("ArrowLeft"), binding("KeyJ")),
    DesktopShortcutAction.SeekForward to listOf(binding("ArrowRight"), binding("KeyL")),
    DesktopShortcutAction.VolumeUp to listOf(binding("ArrowUp")),
    DesktopShortcutAction.VolumeDown to listOf(binding("ArrowDown")),
    DesktopShortcutAction.Fullscreen to listOf(binding("F11"), binding("KeyF")),
    DesktopShortcutAction.SkipIntro to listOf(binding("Enter")),
    DesktopShortcutAction.SubtitleSelector to listOf(binding("KeyS")),
    DesktopShortcutAction.SourceList to listOf(binding("KeyQ")),
    DesktopShortcutAction.EpisodeList to listOf(binding("KeyE")),
    DesktopShortcutAction.NextEpisode to listOf(binding("KeyN", shift = true)),
)

internal fun KeyEvent.toDesktopShortcutBinding(): DesktopShortcutBinding? =
    key.toDesktopShortcutCode()?.let { code ->
        DesktopShortcutBinding(
            code = code,
            shift = isShiftPressed,
            ctrl = isCtrlPressed,
            alt = isAltPressed,
            meta = isMetaPressed,
        )
    }

internal fun DesktopShortcutBinding.matches(event: KeyEvent): Boolean =
    code == event.key.toDesktopShortcutCode() &&
        shift == event.isShiftPressed &&
        ctrl == event.isCtrlPressed &&
        alt == event.isAltPressed &&
        meta == event.isMetaPressed

private fun binding(code: String, shift: Boolean = false): DesktopShortcutBinding =
    DesktopShortcutBinding(code = code, shift = shift)

private fun Key.toDesktopShortcutCode(): String? = when (this) {
    Key.One -> "Digit1"
    Key.Two -> "Digit2"
    Key.Three -> "Digit3"
    Key.Four -> "Digit4"
    Key.Five -> "Digit5"
    Key.Six -> "Digit6"
    Key.Seven -> "Digit7"
    Key.Eight -> "Digit8"
    Key.Nine -> "Digit9"
    Key.Zero -> "Digit0"
    Key.NumPad0 -> "Numpad0"
    Key.NumPad1 -> "Numpad1"
    Key.NumPad2 -> "Numpad2"
    Key.NumPad3 -> "Numpad3"
    Key.NumPad4 -> "Numpad4"
    Key.NumPad5 -> "Numpad5"
    Key.NumPad6 -> "Numpad6"
    Key.NumPad7 -> "Numpad7"
    Key.NumPad8 -> "Numpad8"
    Key.NumPad9 -> "Numpad9"
    Key.NumPadDivide -> "NumpadDivide"
    Key.NumPadMultiply -> "NumpadMultiply"
    Key.NumPadSubtract -> "NumpadSubtract"
    Key.NumPadAdd -> "NumpadAdd"
    Key.NumPadDot -> "NumpadDecimal"
    Key.NumPadComma -> "NumpadComma"
    Key.NumPadEnter -> "NumpadEnter"
    Key.NumPadEquals -> "NumpadEqual"
    Key.Plus -> "Equal"
    Key.Minus -> "Minus"
    Key.Multiply -> "NumpadMultiply"
    Key.Equals -> "Equal"
    Key.Comma -> "Comma"
    Key.Period -> "Period"
    Key.Slash -> "Slash"
    Key.Tab -> "Tab"
    Key.Backspace -> "Backspace"
    Key.Delete -> "Delete"
    Key.MoveHome -> "Home"
    Key.MoveEnd -> "End"
    Key.Insert -> "Insert"
    Key.PageUp -> "PageUp"
    Key.PageDown -> "PageDown"
    Key.Grave -> "Backquote"
    Key.LeftBracket -> "BracketLeft"
    Key.RightBracket -> "BracketRight"
    Key.Backslash -> "Backslash"
    Key.Semicolon -> "Semicolon"
    Key.Apostrophe -> "Quote"
    Key.Escape -> "Escape"
    Key.Spacebar -> "Space"
    Key.Enter -> "Enter"
    Key.F11 -> "F11"
    Key.DirectionLeft -> "ArrowLeft"
    Key.DirectionRight -> "ArrowRight"
    Key.DirectionUp -> "ArrowUp"
    Key.DirectionDown -> "ArrowDown"
    Key.A -> "KeyA"
    Key.B -> "KeyB"
    Key.C -> "KeyC"
    Key.D -> "KeyD"
    Key.E -> "KeyE"
    Key.F -> "KeyF"
    Key.G -> "KeyG"
    Key.H -> "KeyH"
    Key.I -> "KeyI"
    Key.J -> "KeyJ"
    Key.K -> "KeyK"
    Key.L -> "KeyL"
    Key.M -> "KeyM"
    Key.N -> "KeyN"
    Key.O -> "KeyO"
    Key.P -> "KeyP"
    Key.Q -> "KeyQ"
    Key.R -> "KeyR"
    Key.S -> "KeyS"
    Key.T -> "KeyT"
    Key.U -> "KeyU"
    Key.V -> "KeyV"
    Key.W -> "KeyW"
    Key.X -> "KeyX"
    Key.Y -> "KeyY"
    Key.Z -> "KeyZ"
    Key.F1 -> "F1"
    Key.F2 -> "F2"
    Key.F3 -> "F3"
    Key.F4 -> "F4"
    Key.F5 -> "F5"
    Key.F6 -> "F6"
    Key.F7 -> "F7"
    Key.F8 -> "F8"
    Key.F9 -> "F9"
    Key.F10 -> "F10"
    Key.F12 -> "F12"
    else -> null
}

private val numpadDisplayNames = mapOf(
    "Divide" to "/",
    "Multiply" to "*",
    "Subtract" to "-",
    "Add" to "+",
    "Decimal" to ".",
    "Comma" to ",",
    "Enter" to "Enter",
    "Equal" to "=",
)

private fun String.displayName(): String = when {
    startsWith("Digit") -> removePrefix("Digit")
    startsWith("Numpad") -> "NumPad ${numpadDisplayNames[removePrefix("Numpad")] ?: removePrefix("Numpad")}"
    startsWith("Key") -> removePrefix("Key")
    else -> when (this) {
        "Equal" -> "="
        "Minus" -> "-"
        "Comma" -> ","
        "Period" -> "."
        "ArrowLeft" -> "←"
        "ArrowRight" -> "→"
        "ArrowUp" -> "↑"
        "ArrowDown" -> "↓"
        "Escape" -> "Esc"
        "PageUp" -> "Page Up"
        "PageDown" -> "Page Down"
        "Backquote" -> "`"
        "BracketLeft" -> "["
        "BracketRight" -> "]"
        "Backslash" -> "\\"
        "Semicolon" -> ";"
        "Quote" -> "'"
        else -> this
    }
}
