package dev.eliaschen.realtime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import dev.eliaschen.realtime.model.NavViewModel
import dev.eliaschen.realtime.model.Screen
import dev.eliaschen.realtime.network.NetworkViewModel
import dev.eliaschen.realtime.screen.CountdownDetail
import dev.eliaschen.realtime.screen.Home
import dev.eliaschen.realtime.ui.theme.RealtimeTheme

val LocalNetwork = compositionLocalOf<NetworkViewModel> { error("network viewmodel") }
val LocalNav = compositionLocalOf<NavViewModel> { error("navigation viewmodel") }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val network: NetworkViewModel by viewModels()
            val navigation: NavViewModel by viewModels()

            CompositionLocalProvider(LocalNetwork provides network,LocalNav provides navigation) {
                RealtimeTheme {
                    when (navigation.currentScreen) {
                        Screen.Home -> Home()
                        Screen.Detail -> CountdownDetail()
                    }
                }
            }
        }
    }
}