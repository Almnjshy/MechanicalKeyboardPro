package com.mkpro.keyboard.ui.screens.home

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mkpro.keyboard.ui.theme.MkBackground
import com.mkpro.keyboard.ui.theme.MkTextPrimary
import com.mkpro.keyboard.ui.theme.MkTextSecondary

/**
 * Real product flow per the corrected spec: the app's main job is guiding
 * the user to enable Mechanical Keyboard Pro as their system keyboard.
 * PC-connection mode is offered as a clearly secondary/optional path.
 */
@Composable
fun HomeScreen(onOpenPcConnectionMode: () -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MkBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Mechanical Keyboard Pro",
            color = MkTextPrimary,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "لوحة مفاتيح ميكانيكية احترافية لهاتفك. فعّلها لتستخدمها في أي تطبيق.",
            color = MkTextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )

        Button(
            onClick = { openInputMethodSettings(context) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("1. تفعيل لوحة المفاتيح من إعدادات أندرويد")
        }

        OutlinedButton(
            onClick = { showInputMethodPicker(context) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("2. اختيارها كلوحة المفاتيح الحالية")
        }

        Text(
            text = "بعد التفعيل، افتح أي تطبيق دردشة أو متصفح واضغط على حقل الكتابة - ستظهر لوحة المفاتيح تلقائيًا.",
            color = MkTextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )

        TextButton(
            onClick = onOpenPcConnectionMode,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("وضع إضافي (اختياري): ربط الهاتف كلوحة مفاتيح لاسلكية بالحاسوب")
        }
    }
}

private fun openInputMethodSettings(context: Context) {
    context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}

private fun showInputMethodPicker(context: Context) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showInputMethodPicker()
}
