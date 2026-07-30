package com.mkpro.keyboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mkpro.keyboard.ui.theme.MkAccentGreen
import com.mkpro.keyboard.ui.theme.MkSurface
import com.mkpro.keyboard.ui.theme.MkTextPrimary
import com.mkpro.keyboard.ui.theme.MkTextSecondary

@Composable
fun LanguageSwitcherRow(languageCode: String, languageName: String, onCycle: () -> Unit) {
    Row(
        modifier = Modifier
            .background(MkSurface, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.size(8.dp).background(MkAccentGreen, CircleShape)
        ) {}
        Text(languageCode, color = MkTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)

        Icon(
            Icons.Filled.ChevronLeft,
            contentDescription = "Previous language",
            tint = MkTextSecondary,
            modifier = Modifier.size(16.dp).clickable { onCycle() }
        )
        Text(languageName, color = MkTextSecondary, fontSize = 12.sp, textAlign = TextAlign.Center)
        Icon(
            Icons.Filled.ChevronRight,
            contentDescription = "Next language",
            tint = MkTextSecondary,
            modifier = Modifier.size(16.dp).clickable { onCycle() }
        )
    }
}
