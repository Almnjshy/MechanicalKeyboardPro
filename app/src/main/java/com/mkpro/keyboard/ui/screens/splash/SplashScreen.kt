package com.mkpro.keyboard.ui.screens.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mkpro.keyboard.ui.theme.MkAccentCyan
import com.mkpro.keyboard.ui.theme.MkAccentMagenta
import com.mkpro.keyboard.ui.theme.MkAccentPurple
import com.mkpro.keyboard.ui.theme.MkBackground
import com.mkpro.keyboard.ui.theme.MkTextPrimary

/**
 * Splash screen: animated glowing logo with a rotating RGB gradient ring
 * and a soft breathing/pulse effect, matching the spec:
 * "Animated logo, RGB glow, Smooth fade animation".
 */
@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    viewModel: SplashViewModel = viewModel()
) {
    // kick off the auto-advance timer once
    LaunchedEffect(Unit) {
        viewModel.awaitSplashDuration(onFinished)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "splashGlow")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Box(contentAlignment = Alignment.Center) {
                // Outer rotating RGB glow ring
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(pulse)
                        .rotate(rotation)
                        .blur(24.dp)
                        .background(
                            brush = Brush.sweepGradient(
                                listOf(MkAccentPurple, MkAccentCyan, MkAccentMagenta, MkAccentPurple)
                            ),
                            shape = CircleShape
                        )
                )
                // Solid inner keycap-like logo mark
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(MkBackground, CircleShape)
                        .padding(4.dp)
                        .background(
                            brush = Brush.linearGradient(listOf(MkAccentPurple, MkAccentCyan)),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "MK",
                        color = Color.White,
                        fontSize = 26.sp,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            Text(
                text = "MECHANICAL KEYBOARD PRO",
                color = MkTextPrimary,
                modifier = Modifier.padding(top = 24.dp),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "PREMIUM • MECHANICAL • RGB",
                color = MkAccentCyan,
                modifier = Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
