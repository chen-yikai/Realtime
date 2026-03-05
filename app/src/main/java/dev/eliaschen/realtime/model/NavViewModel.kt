package dev.eliaschen.realtime.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class Screen {
    Home, Detail
}

class NavViewModel : ViewModel() {
    private val initScreen = Screen.Home
    var currentScreen by mutableStateOf(initScreen)
        private set
    var navExtra by mutableStateOf("")

    fun navigate(screen: Screen, extra: String = "") {
        if (extra.isNotEmpty()) navExtra = extra
        currentScreen = screen
    }
}