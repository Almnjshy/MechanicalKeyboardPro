package com.mkpro.keyboard.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mkpro.keyboard.MkProApplication
import com.mkpro.keyboard.core.rgb.RgbEffectType
import com.mkpro.keyboard.core.settings.AppSettings
import com.mkpro.keyboard.core.settings.ThemeVariant
import com.mkpro.keyboard.ui.theme.MkAccentCyan
import com.mkpro.keyboard.ui.theme.MkBackground
import com.mkpro.keyboard.ui.theme.MkSurface
import com.mkpro.keyboard.ui.theme.MkTextPrimary
import com.mkpro.keyboard.ui.theme.MkTextSecondary
import com.mkpro.keyboard.ui.theme.paletteFor
import kotlinx.coroutines.launch

private fun ThemeVariant.displayName(): String = when (this) {
    ThemeVariant.DARK -> "Dark"
    ThemeVariant.LIGHT -> "Light"
    ThemeVariant.CYBERPUNK -> "Cyberpunk"
    ThemeVariant.NEON -> "Neon"
    ThemeVariant.MINIMAL -> "Minimal"
    ThemeVariant.CLASSIC -> "Classic"
    ThemeVariant.GLASS -> "Glass"
    ThemeVariant.CARBON_FIBER -> "Carbon Fiber"
}

private fun RgbEffectType.displayName(): String = when (this) {
    RgbEffectType.STATIC -> "ثابت"
    RgbEffectType.BREATHING -> "تنفّس"
    RgbEffectType.WAVE -> "موجة"
    RgbEffectType.REACTIVE -> "تفاعلي"
    RgbEffectType.RIPPLE -> "تموّج"
    RgbEffectType.RAINBOW -> "قوس قزح"
}

private val RgbPresetColors = listOf(
    0xFF9B5CFF.toInt(), 0xFF00E5FF.toInt(), 0xFFFF2DA6.toInt(),
    0xFF35FFA0.toInt(), 0xFFFFB020.toInt(), 0xFFFF4D5E.toInt(), 0xFFFFFFFF.toInt()
)

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val settingsRepository = remember { (context.applicationContext as MkProApplication).settingsRepository }
    val scope = rememberCoroutineScope()
    val settings by settingsRepository.settingsFlow.collectAsState(initial = AppSettings())

    fun update(block: (AppSettings) -> AppSettings) {
        scope.launch { settingsRepository.update(block(settings)) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MkBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("الإعدادات", color = MkTextPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineMedium)

        // --- Appearance: theme picker ---
        SettingsSection(title = "المظهر") {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .size(220.dp)
            ) {
                items(ThemeVariant.entries) { variant ->
                    val palette = paletteFor(variant)
                    val isSelected = settings.theme == variant
                    Column(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(palette.background, RoundedCornerShape(12.dp))
                            .clickable { update { it.copy(theme = variant) } }
                            .then(
                                if (isSelected) Modifier.background(MkAccentCyan.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                else Modifier
                            )
                            .padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            listOf(palette.accentPurple, palette.accentCyan, palette.accentMagenta).forEach { c ->
                                Column(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .background(c, RoundedCornerShape(3.dp))
                                ) {}
                            }
                        }
                        Text(
                            text = variant.displayName(),
                            color = palette.textPrimary,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        }

        // --- RGB ---
        SettingsSection(title = "إضاءة RGB") {
            Text("التأثير", color = MkTextSecondary, style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RgbEffectType.entries.forEach { effect ->
                    val isSelected = settings.rgbEffect == effect
                    Text(
                        text = effect.displayName(),
                        color = if (isSelected) MkTextPrimary else MkTextSecondary,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .background(
                                if (isSelected) MkAccentCyan.copy(alpha = 0.25f) else MkSurface,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { update { it.copy(rgbEffect = effect) } }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Text("اللون", color = MkTextSecondary, style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                RgbPresetColors.forEach { argb ->
                    val isSelected = settings.rgbColorArgb == argb
                    Column(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(argb))
                            .then(
                                if (isSelected) Modifier.background(MkAccentCyan.copy(alpha = 0.3f), CircleShape)
                                else Modifier
                            )
                            .clickable { update { it.copy(rgbColorArgb = argb) } }
                    ) {}
                }
            }

            Text("السطوع", color = MkTextSecondary, style = MaterialTheme.typography.bodyMedium)
            Slider(
                value = settings.rgbBrightness,
                onValueChange = { update { s -> s.copy(rgbBrightness = it) } },
                valueRange = 0.2f..1f
            )

            Text("السرعة", color = MkTextSecondary, style = MaterialTheme.typography.bodyMedium)
            Slider(
                value = settings.rgbSpeed,
                onValueChange = { update { s -> s.copy(rgbSpeed = it) } },
                valueRange = 0.3f..3f
            )
        }

        // --- Sound & Haptics ---
        SettingsSection(title = "الصوت والاهتزاز") {
            SettingsToggleRow(
                label = "صوت الضغط على المفاتيح",
                checked = settings.soundEnabled,
                onCheckedChange = { update { s -> s.copy(soundEnabled = it) } }
            )
            SettingsToggleRow(
                label = "الاهتزاز عند الضغط",
                checked = settings.vibrationEnabled,
                onCheckedChange = { update { s -> s.copy(vibrationEnabled = it) } }
            )
        }

        // --- Appearance: opacity ---
        SettingsSection(title = "شفافية لوحة المفاتيح") {
            Slider(
                value = settings.keyboardOpacity,
                onValueChange = { update { s -> s.copy(keyboardOpacity = it) } },
                valueRange = 0.4f..1f
            )
        }

        OutlinedButton(onClick = { update { AppSettings() } }) {
            Text("استعادة الإعدادات الافتراضية")
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MkSurface, RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(title, color = MkTextPrimary, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
        content()
    }
}

@Composable
private fun SettingsToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MkTextSecondary, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
