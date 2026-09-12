package com.nuvio.app.features.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nuvio.app.core.ui.DesktopShortcutAction
import com.nuvio.app.core.ui.DesktopShortcutBinding
import com.nuvio.app.core.ui.DesktopShortcutSection
import com.nuvio.app.core.ui.isDisabled
import com.nuvio.app.core.ui.nuvio
import com.nuvio.app.core.ui.toDesktopShortcutBinding
import nuvio.composeapp.generated.resources.Res
import nuvio.composeapp.generated.resources.action_cancel
import nuvio.composeapp.generated.resources.action_delete
import nuvio.composeapp.generated.resources.action_reset
import nuvio.composeapp.generated.resources.compose_settings_shortcuts_back
import nuvio.composeapp.generated.resources.compose_settings_shortcuts_already_in_use
import nuvio.composeapp.generated.resources.compose_settings_shortcuts_episode_list
import nuvio.composeapp.generated.resources.compose_settings_shortcuts_fullscreen
import nuvio.composeapp.generated.resources.compose_settings_shortcuts_home
import nuvio.composeapp.generated.resources.compose_settings_shortcuts_library
import nuvio.composeapp.generated.resources.compose_settings_shortcuts_mute
import nuvio.composeapp.generated.resources.compose_settings_shortcuts_next_episode
import nuvio.composeapp.generated.resources.compose_settings_shortcuts_play_pause
import nuvio.composeapp.generated.resources.compose_settings_shortcuts_press_key
import nuvio.composeapp.generated.resources.compose_settings_shortcuts_search
import nuvio.composeapp.generated.resources.compose_settings_shortcuts_search_box
import nuvio.composeapp.generated.resources.compose_settings_shortcuts_section_general
import nuvio.composeapp.generated.resources.compose_settings_shortcuts_section_player
import nuvio.composeapp.generated.resources.compose_settings_shortcuts_seek_back
import nuvio.composeapp.generated.resources.compose_settings_shortcuts_seek_forward
import nuvio.composeapp.generated.resources.compose_settings_shortcuts_settings
import nuvio.composeapp.generated.resources.compose_settings_shortcuts_skip_intro
import nuvio.composeapp.generated.resources.compose_settings_shortcuts_source_list
import nuvio.composeapp.generated.resources.compose_settings_shortcuts_subtitle_selector
import nuvio.composeapp.generated.resources.compose_settings_shortcuts_subtitle_toggle
import nuvio.composeapp.generated.resources.compose_settings_shortcuts_volume_down
import nuvio.composeapp.generated.resources.compose_settings_shortcuts_volume_up
import nuvio.composeapp.generated.resources.settings_playback_option_none
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private data class ShortcutRowSpec(
    val title: StringResource,
    val action: DesktopShortcutAction,
)

private data class ShortcutSectionSpec(
    val title: StringResource,
    val rows: List<ShortcutRowSpec>,
)

private data class CapturingSlot(
    val action: DesktopShortcutAction,
    val index: Int,
)

private fun shortcutSections(): List<ShortcutSectionSpec> =
    DesktopShortcutSection.values().map { section ->
        ShortcutSectionSpec(
            title = section.titleResource(),
            rows = DesktopShortcutAction.values()
                .filter { it.section == section }
                .map { action -> ShortcutRowSpec(action.titleResource(), action) },
        )
    }

private fun DesktopShortcutSection.titleResource(): StringResource = when (this) {
    DesktopShortcutSection.General -> Res.string.compose_settings_shortcuts_section_general
    DesktopShortcutSection.Player -> Res.string.compose_settings_shortcuts_section_player
}

private fun DesktopShortcutAction.titleResource(): StringResource = when (this) {
    DesktopShortcutAction.Back -> Res.string.compose_settings_shortcuts_back
    DesktopShortcutAction.HomeTab -> Res.string.compose_settings_shortcuts_home
    DesktopShortcutAction.SearchTab -> Res.string.compose_settings_shortcuts_search
    DesktopShortcutAction.LibraryTab -> Res.string.compose_settings_shortcuts_library
    DesktopShortcutAction.SettingsTab -> Res.string.compose_settings_shortcuts_settings
    DesktopShortcutAction.FocusSearch -> Res.string.compose_settings_shortcuts_search_box
    DesktopShortcutAction.PlayPause -> Res.string.compose_settings_shortcuts_play_pause
    DesktopShortcutAction.SubtitleToggle -> Res.string.compose_settings_shortcuts_subtitle_toggle
    DesktopShortcutAction.Mute -> Res.string.compose_settings_shortcuts_mute
    DesktopShortcutAction.SeekBack -> Res.string.compose_settings_shortcuts_seek_back
    DesktopShortcutAction.SeekForward -> Res.string.compose_settings_shortcuts_seek_forward
    DesktopShortcutAction.VolumeUp -> Res.string.compose_settings_shortcuts_volume_up
    DesktopShortcutAction.VolumeDown -> Res.string.compose_settings_shortcuts_volume_down
    DesktopShortcutAction.Fullscreen -> Res.string.compose_settings_shortcuts_fullscreen
    DesktopShortcutAction.SkipIntro -> Res.string.compose_settings_shortcuts_skip_intro
    DesktopShortcutAction.SubtitleSelector -> Res.string.compose_settings_shortcuts_subtitle_selector
    DesktopShortcutAction.SourceList -> Res.string.compose_settings_shortcuts_source_list
    DesktopShortcutAction.EpisodeList -> Res.string.compose_settings_shortcuts_episode_list
    DesktopShortcutAction.NextEpisode -> Res.string.compose_settings_shortcuts_next_episode
}

internal fun LazyListScope.shortcutsSettingsContent(isTablet: Boolean) {
    item(key = "customizable-shortcuts") {
        CustomizableShortcutsContent(isTablet = isTablet)
    }
}

@Composable
private fun CustomizableShortcutsContent(isTablet: Boolean) {
    val settings = DesktopShortcutSettingsRepository
    val state by settings.uiState.collectAsStateWithLifecycle()
    var capturingSlot by remember { mutableStateOf<CapturingSlot?>(null) }
    var conflictSlot by remember { mutableStateOf<CapturingSlot?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        shortcutSections().forEach { section ->
            SettingsSection(
                title = stringResource(section.title),
                isTablet = isTablet,
            ) {
                SettingsGroup(isTablet = isTablet) {
                    section.rows.forEachIndexed { rowIndex, row ->
                        val bindings = state.bindings[row.action].orEmpty()
                        ShortcutRow(
                            title = stringResource(row.title),
                            action = row.action,
                            isTablet = isTablet,
                            bindings = bindings,
                            capturingSlot = capturingSlot,
                            conflictSlot = conflictSlot,
                            onCaptureStart = { slot ->
                                conflictSlot = null
                                capturingSlot = slot
                            },
                            onCaptureCancel = { capturingSlot = null },
                            onBindingCaptured = { slot, binding ->
                                if (settings.setBinding(slot.action, slot.index, binding)) {
                                    conflictSlot = null
                                    capturingSlot = null
                                } else {
                                    conflictSlot = slot
                                }
                            },
                            onReset = {
                                settings.resetAction(row.action)
                                capturingSlot = null
                                conflictSlot = null
                            },
                            onRemove = { slotIndex ->
                                settings.removeBinding(row.action, slotIndex)
                                capturingSlot = null
                                conflictSlot = null
                            },
                        )
                        if (rowIndex < section.rows.lastIndex) {
                            SettingsGroupDivider(isTablet = isTablet)
                        }
                    }
                }
            }
        }
        TextButton(onClick = {
            settings.resetAll()
            capturingSlot = null
            conflictSlot = null
        }) {
            Text(stringResource(Res.string.action_reset))
        }
    }
}

@Composable
private fun ShortcutRow(
    title: String,
    action: DesktopShortcutAction,
    isTablet: Boolean,
    bindings: List<DesktopShortcutBinding>,
    capturingSlot: CapturingSlot?,
    conflictSlot: CapturingSlot?,
    onCaptureStart: (CapturingSlot) -> Unit,
    onCaptureCancel: () -> Unit,
    onBindingCaptured: (CapturingSlot, DesktopShortcutBinding) -> Unit,
    onReset: () -> Unit,
    onRemove: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = if (isTablet) 20.dp else 16.dp,
                vertical = if (isTablet) 14.dp else 12.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.nuvio.colors.textPrimary,
            )
            TextButton(onClick = onReset) {
                Text(stringResource(Res.string.action_reset))
            }
        }
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            bindings.forEachIndexed { index, binding ->
                val slot = CapturingSlot(action, index)
                ShortcutBindingChip(
                    binding = binding,
                    isCapturing = capturingSlot == slot,
                    hasConflict = conflictSlot == slot,
                    onCaptureStart = { onCaptureStart(slot) },
                    onCaptureCancel = onCaptureCancel,
                    onBindingCaptured = { captured -> onBindingCaptured(slot, captured) },
                    onRemove = { onRemove(index) },
                )
            }
        }
    }
}

@Composable
private fun ShortcutBindingChip(
    binding: DesktopShortcutBinding,
    isCapturing: Boolean,
    hasConflict: Boolean,
    onCaptureStart: () -> Unit,
    onCaptureCancel: () -> Unit,
    onBindingCaptured: (DesktopShortcutBinding) -> Unit,
    onRemove: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    Row(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = if (hasConflict) MaterialTheme.colorScheme.error else MaterialTheme.nuvio.colors.borderSubtle,
                shape = RoundedCornerShape(6.dp),
            )
            .padding(start = 8.dp, end = if (isCapturing) 2.dp else 8.dp, top = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = when {
                isCapturing -> stringResource(Res.string.compose_settings_shortcuts_press_key)
                hasConflict -> stringResource(Res.string.compose_settings_shortcuts_already_in_use)
                binding.isDisabled -> stringResource(Res.string.settings_playback_option_none)
                else -> binding.displayValue()
            },
            modifier = Modifier
                .focusRequester(focusRequester)
                .focusable()
                .onPreviewKeyEvent { event ->
                    if (!isCapturing || event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                    // Escape is a valid binding. Capture mode is cancelled with the close button.
                    event.toDesktopShortcutBinding()?.let(onBindingCaptured)
                    true
                }
                .clickable {
                    if (isCapturing) {
                        onCaptureCancel()
                    } else {
                        onCaptureStart()
                        focusRequester.requestFocus()
                    }
                },
            style = MaterialTheme.typography.labelLarge,
            color = if (hasConflict) MaterialTheme.colorScheme.error else MaterialTheme.nuvio.colors.textPrimary,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
        if (isCapturing) {
            IconButton(
                onClick = onCaptureCancel,
                modifier = Modifier.size(28.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = stringResource(Res.string.action_cancel),
                )
            }
        } else if (!binding.isDisabled) {
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(28.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = stringResource(Res.string.action_delete),
                )
            }
        }
    }
}
