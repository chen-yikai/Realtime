package dev.eliaschen.realtime.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.eliaschen.realtime.LocalNav
import dev.eliaschen.realtime.LocalNetwork
import dev.eliaschen.realtime.component.CustomCard
import dev.eliaschen.realtime.isoTimeFormatter
import dev.eliaschen.realtime.model.Screen
import dev.eliaschen.realtime.network.Time
import dev.eliaschen.realtime.timeLeft

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Home(modifier: Modifier = Modifier) {
    val network = LocalNetwork.current
    val nav = LocalNav.current
    val times = network.times

    Scaffold(floatingActionButton = {
        ExtendedFloatingActionButton(
            text = { Text("New Time") },
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            onClick = {})
    }) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            stickyHeader {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 20.dp)
                        .padding(top = 10.dp), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "RealTime",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            item {
                Spacer(Modifier.height(10.dp))
            }
            items(times) { time ->
                CustomCard(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    onClick = { nav.navigate(Screen.Detail, time.id) }) {
                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(time.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(
                                time.createdAt.isoTimeFormatter(),
                                fontSize = 12.sp,
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                time.targetTime.timeLeft().day.toString(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp, lineHeight = 20.sp
                            )
                            Text("天", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}