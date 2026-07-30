package com.mkpro.keyboard.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SPLASH_DURATION_MS = 1800L

class SplashViewModel : ViewModel() {

    fun awaitSplashDuration(onFinished: () -> Unit) {
        viewModelScope.launch {
            delay(SPLASH_DURATION_MS)
            onFinished()
        }
    }
}
