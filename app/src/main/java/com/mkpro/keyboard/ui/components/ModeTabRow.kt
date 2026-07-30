package com.mkpro.keyboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TagFaces
import androidx.compose.material3.Icon
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mkpro.keyboard.ui.theme.MkSurface
import com.mkpro.keyboard.ui.theme.MkTextSecondary

private data class ModeTab(val icon: ImageVector, val id: String, val onClick: () -> Unit)

/**
 * The icon set shown here changes with the active layer, mirroring the
 * reference design: typing layers show Keyboard/Theme/Clipboard/Emoji,
 * PC Keys swaps Theme for Layers, Gaming shows Game/Macro/Profile/RGB,
 * Programming shows Code/Clipboard/AI/Snippets. Only the keyboard-icon tab
 * (always first, always "active") is meaningfully wired right now - the
 * rest route to Settings or are visual placeholders for features not built
 * yet (clipboard manager, emoji picker, AI tools, snippets) - see README.
 */
@Composable
fun ModeTabRow(
    activeLayerId: String,
    accentColor: Color,
    onOpenSettings: () -> Unit
) {
    val tabs = when (activeLayerId) {
        "pc_keys" -> listOf(
            ModeTab(Icons.Filled.Keyboard, "keyboard") {},
            ModeTab(Icons.Filled.Layers, "layers", onOpenSettings),
            ModeTab(Icons.Filled.ContentPaste, "clipboard", onOpenSettings),
            ModeTab(Icons.Filled.TagFaces, "emoji", onOpenSettings)
        )
        "gaming" -> listOf(
            ModeTab(Icons.Filled.SportsEsports, "game") {},
            ModeTab(Icons.Filled.Bolt, "macro", onOpenSettings),
            ModeTab(Icons.Filled.Person, "profile", onOpenSettings),
            ModeTab(Icons.Filled.Palette, "rgb", onOpenSettings)
        )
        "programming" -> listOf(
            ModeTab(Icons.Filled.Code, "code") {},
            ModeTab(Icons.Filled.ContentPaste, "clipboard", onOpenSettings),
            ModeTab(Icons.Filled.AutoAwesome, "ai", onOpenSettings),
            ModeTab(Icons.Filled.Bolt, "snippets", onOpenSettings)
        )
        else -> listOf(
            ModeTab(Icons.Filled.Keyboard, "keyboard") {},
            ModeTab(Icons.Filled.Palette, "theme", onOpenSettings),
            ModeTab(Icons.Filled.ContentPaste, "clipboard", onOpenSettings),
            ModeTab(Icons.Filled.TagFaces, "emoji", onOpenSettings)
        )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        tabs.forEachIndexed { index, tab ->
            val isActive = index == 0 // the mode-matching icon (first) is always "active" for the current layer
            Row(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        if (isActive) accentColor.copy(alpha = 0.18f) else MkSurface,
                        RoundedCornerShape(9.dp)
                    )
                    .clickable { tab.onClick() }
                    .padding(6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = tab.icon,
                    contentDescription = tab.id,
                    tint = if (isActive) accentColor else MkTextSecondary
                )
            }
        }
    }
}
