package com.mkpro.keyboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mkpro.keyboard.ui.theme.MkSurface
import com.mkpro.keyboard.ui.theme.MkTextSecondary

private data class QuickAction(val icon: ImageVector, val label: String, val onClick: () -> Unit)

/**
 * The prominent labeled pill row that appears under the compact icon tab
 * row for non-typing modes (Gaming: Game/Macro/Profile/RGB, Programming:
 * Code/Clipboard/AI Tools/Snippets, PC Keys: Layers/Clipboard/Emoji). Only
 * the first pill (matching the current mode) is "active" - the rest route
 * to Settings for now (see README: clipboard/AI/snippets aren't built yet).
 */
@Composable
fun QuickActionRow(layerId: String, accentColor: Color, onOpenSettings: () -> Unit) {
    val actions = when (layerId) {
        "gaming" -> listOf(
            QuickAction(Icons.Filled.SportsEsports, "GAME") {},
            QuickAction(Icons.Filled.Bolt, "MACRO", onOpenSettings),
            QuickAction(Icons.Filled.Person, "PROFILE", onOpenSettings),
            QuickAction(Icons.Filled.Palette, "RGB", onOpenSettings)
        )
        "programming" -> listOf(
            QuickAction(Icons.Filled.Code, "CODE") {},
            QuickAction(Icons.Filled.ContentPaste, "CLIPBOARD", onOpenSettings),
            QuickAction(Icons.Filled.AutoAwesome, "AI TOOLS", onOpenSettings),
            QuickAction(Icons.Filled.Bolt, "SNIPPETS", onOpenSettings)
        )
        "pc_keys" -> listOf(
            QuickAction(Icons.Filled.Layers, "LAYERS") {},
            QuickAction(Icons.Filled.ContentPaste, "CLIPBOARD", onOpenSettings)
        )
        else -> emptyList()
    }
    if (actions.isEmpty()) return

    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        actions.forEachIndexed { index, action ->
            val isActive = index == 0
            Row(
                modifier = Modifier
                    .background(
                        if (isActive) accentColor.copy(alpha = 0.18f) else MkSurface,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { action.onClick() }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = action.label,
                    tint = if (isActive) accentColor else MkTextSecondary,
                    modifier = Modifier.padding(0.dp)
                )
                Text(
                    text = action.label,
                    color = if (isActive) accentColor else MkTextSecondary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
            }
        }
    }
}
