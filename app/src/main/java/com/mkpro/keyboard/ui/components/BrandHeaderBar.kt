package com.mkpro.keyboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mkpro.keyboard.ui.theme.MkTextSecondary

/**
 * Thin, icon-only control bar (~38dp) - deliberately no app name/branding
 * text, per spec: "Do NOT display a header containing the application
 * name... every pixel should serve a purpose." Just hamburger on the left,
 * settings + expand/collapse circles on the right, both tinted with the
 * active mode's accent color.
 */
@Composable
fun BrandHeaderBar(
    accentColor: Color,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    onOpenMenu: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            Icons.Filled.Menu,
            contentDescription = "Menu",
            tint = MkTextSecondary,
            modifier = Modifier
                .size(20.dp)
                .clickable { onOpenMenu() }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CircleIconButton(icon = Icons.Filled.Settings, accentColor = accentColor, onClick = onOpenSettings)
            CircleIconButton(
                icon = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                accentColor = accentColor,
                onClick = onToggleExpanded
            )
        }
    }
}

@Composable
private fun CircleIconButton(icon: ImageVector, accentColor: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .size(28.dp)
            .background(accentColor.copy(alpha = 0.15f), CircleShape)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
    }
}
