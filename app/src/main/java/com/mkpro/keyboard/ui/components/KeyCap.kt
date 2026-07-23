package com.mkpro.keyboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mkpro.keyboard.core.keyboard.KeyModel
import com.mkpro.keyboard.ui.theme.MkBorder
import com.mkpro.keyboard.ui.theme.MkKeycap
import com.mkpro.keyboard.ui.theme.MkKeycapPressed
import com.mkpro.keyboard.ui.theme.MkTextPrimary

/**
 * A single mechanical keycap: fills its weighted share of the row.
 * Simplified version without animations for IME stability.
 */
@Composable
fun RowScope.KeyCap(
    key: KeyModel,
    accentColor: Color? = null,
    onPress: (KeyModel) -> Unit
) {
    Box(
        modifier = Modifier
            .weight(key.widthWeight.coerceAtLeast(0.1f))  // Prevent 0 or negative weight
            .fillMaxHeight()
            .padding(3.dp)
            .background(
                color = MkKeycap,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = if (accentColor != null) 1.5.dp else 1.dp,
                color = accentColor ?: MkBorder,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onPress(key) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = key.label,
            color = MkTextPrimary,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
