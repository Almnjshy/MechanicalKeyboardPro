package com.mkpro.keyboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mkpro.keyboard.ui.theme.MkSurface
import com.mkpro.keyboard.ui.theme.MkTextSecondary

/**
 * Globe (cycles language/layer) + centered pill showing the active mode
 * name in that mode's accent color + mic icon. The mic is a visual
 * placeholder only - voice input isn't wired up (would need
 * RecognizerIntent/SpeechRecognizer integration, not built yet).
 */
@Composable
fun BottomActionBar(modeName: String, accentColor: Color, onGlobeClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            Icons.Filled.Language,
            contentDescription = "Switch language",
            tint = MkTextSecondary,
            modifier = Modifier
                .size(22.dp)
                .clickable { onGlobeClick() }
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .background(MkSurface, RoundedCornerShape(10.dp))
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = modeName.uppercase(),
                color = accentColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }

        Icon(
            Icons.Filled.Mic,
            contentDescription = "Voice input",
            tint = MkTextSecondary,
            modifier = Modifier.size(22.dp)
        )
    }
}
