package dev.eliaschen.realtime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransition = compositionLocalOf<SharedTransitionScope> { error("shared transition") }
val LocalAnimated = compositionLocalOf<AnimatedVisibilityScope> { error("animated") }

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val network: NetworkViewModel by viewModels()
            val navigation: NavViewModel by viewModels()

            RealtimeTheme {
                SharedTransitionLayout {
                    AnimatedContent(navigation.currentScreen, transitionSpec = {
                        when (targetState) {
                            Screen.Home -> fadeIn() togetherWith slideOutHorizontally { it }
                            Screen.Detail -> slideInHorizontally { it } togetherWith fadeOut()
                        }
                    }) {
                        CompositionLocalProvider(
                            LocalNetwork provides network,
                            LocalNav provides navigation,
                            LocalSharedTransition provides this@SharedTransitionLayout,
                            LocalAnimated provides this@AnimatedContent
                        ) {
                            when (it) {
                                Screen.Home -> Home()
                                Screen.Detail -> CountdownDetail()
                            }
                        }
                    }
                }
            }
        }
    }
}