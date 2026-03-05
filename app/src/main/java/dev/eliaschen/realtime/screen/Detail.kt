package dev.eliaschen.realtime.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.eliaschen.realtime.LocalNav
import dev.eliaschen.realtime.LocalNetwork
import dev.eliaschen.realtime.component.CustomCard
import dev.eliaschen.realtime.model.Screen
import dev.eliaschen.realtime.network.Countdown
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountdownDetail(modifier: Modifier = Modifier) {
    val nav = LocalNav.current
    val network = LocalNetwork.current
    val time = network.times.first { it.id == nav.navExtra }
    var countdown by remember { mutableStateOf<Countdown?>(null) }
    var ended by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        network.getCountdownStream(time.id).collectLatest {
            countdown = it
        }
    }

    Scaffold(topBar = {
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 10.dp)
        ) {
            IconButton(onClick = {
                nav.navigate(
                    Screen.Home
                )
            }) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
        }
    }) { innerPadding ->
        innerPadding
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(if (ended) "已結束" else "距離", fontWeight = FontWeight.Bold)
                    Text(time.title, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                }
                AnimatedVisibility(countdown != null) {
                    val countdownParts = mapOf(
                        "天" to countdown!!.day,
                        "小時" to countdown!!.hour,
                        "分鐘" to countdown!!.minute,
                        "秒" to countdown!!.second
                    )
                    ended =
                        countdownParts.filter { (unit, value) -> value == 0 }.size == countdownParts.size
                    if (!ended) {
                        CustomCard {
                            Row(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                countdownParts.forEach { (unit, value) ->
                                    if (value != 0) {
                                        CountdownLabel(value, unit)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CountdownLabel(value: Int, unit: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .widthIn(min = 60.dp)
    ) {
        Text(value.toString().padStart(2, '0'), fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Text(unit)
    }
}