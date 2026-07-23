package com.mkpro.keyboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mkpro.keyboard.ui.theme.MkAccentGreen
import com.mkpro.keyboard.ui.theme.MkSurface
import com.mkpro.keyboard.ui.theme.MkTextPrimary
import com.mkpro.keyboard.ui.theme.MkTextSecondary

@Composable
fun CommandBar(
    isConnected: Boolean,
    currentLayerName: String,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenRgb: () -> Unit,
    onOpenProfile: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MkSurface, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = if (isConnected) "PC CONNECTED" else "DISCONNECTED",
                color = if (isConnected) MkAccentGreen else MkTextSecondary,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Text(text = currentLayerName.uppercase(), color = MkTextPrimary, style = MaterialTheme.typography.labelSmall)

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onOpenProfile) {
                Icon(Icons.Filled.Person, contentDescription = "Profile", tint = MkTextPrimary)
            }
            IconButton(onClick = onOpenRgb) {
                Icon(Icons.Filled.Palette, contentDescription = "RGB", tint = MkTextPrimary)
            }
            IconButton(onClick = onOpenSettings) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = MkTextPrimary)
            }
            IconButton(onClick = onToggleExpanded) {
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Toggle advanced panel",
                    tint = MkTextPrimary
                )
            }
        }
    }
}
