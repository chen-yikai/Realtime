package dev.eliaschen.realtime.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
                    Text("距離", fontWeight = FontWeight.Bold)
                    Text(time.title, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                }
                countdown?.let {
                    CustomCard {
                        Row(
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            mapOf(
                                "天" to it.day, "小時" to it.hour, "分鐘" to it.minute,
                                "秒" to it.second
                            ).forEach { (unit, value) ->
                                CountdownLabel(value, unit)
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
    if (value != 0) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .widthIn(min = 60.dp)
        ) {
            Text(value.toString().padStart(2, '0'), fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Text(unit)
        }
    }
}