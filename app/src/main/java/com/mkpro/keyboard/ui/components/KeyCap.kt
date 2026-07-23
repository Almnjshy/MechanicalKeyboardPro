package com.mkpro.keyboard.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
 * A single mechanical keycap: fills its weighted share of the row, shows a
 * short "press" travel animation and an optional RGB glow border driven by
 * accentColor (per-key RGB override / reactive effect).
 */
@Composable
fun RowScope.KeyCap(
    key: KeyModel,
    accentColor: Color? = null,
    onPress: (KeyModel) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val travel by animateDpAsState(targetValue = if (isPressed) 3.dp else 0.dp, label = "keyTravel")

    Box(
        modifier = Modifier
            .weight(key.widthWeight)
            .fillMaxHeight()
            .padding(3.dp)
            .padding(top = travel)
            .background(
                color = if (isPressed) MkKeycapPressed else MkKeycap,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = if (accentColor != null) 1.5.dp else 1.dp,
                color = accentColor ?: MkBorder,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null) { onPress(key) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = key.label,
            color = MkTextPrimary,
            style = MaterialTheme.typography.labelSmall
        )
    }
}