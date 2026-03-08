package dev.eliaschen.realtime.screen

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.eliaschen.realtime.LocalAnimated
import dev.eliaschen.realtime.LocalNav
import dev.eliaschen.realtime.LocalNetwork
import dev.eliaschen.realtime.LocalSharedTransition
import dev.eliaschen.realtime.component.CustomCard
import dev.eliaschen.realtime.component.NewTimeSheet
import dev.eliaschen.realtime.isoTimeFormatter
import dev.eliaschen.realtime.model.Screen
import dev.eliaschen.realtime.network.Time
import dev.eliaschen.realtime.timeLeft

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
fun Home(modifier: Modifier = Modifier) {
    val network = LocalNetwork.current
    val times = network.times
    var showBottomSheet by remember { mutableStateOf(false) }
    var editTimeId by remember { mutableStateOf("") }

    if (showBottomSheet) {
        NewTimeSheet(editTimeId) {
            showBottomSheet = false
        }
    }

    Scaffold(floatingActionButton = {
        ExtendedFloatingActionButton(
            text = { Text("New Time") },
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            onClick = {
                editTimeId = ""
                showBottomSheet = true
            })
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
            items(times, { it.id }) { time ->
                DismissableTimeItem(time) {
                    editTimeId = time.id
                    showBottomSheet = true
                }
            }
        }
    }
}

@Composable
fun DismissableTimeItem(time: Time, editTime: () -> Unit) {
    val network = LocalNetwork.current
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    editTime()
                    false
                }

                SwipeToDismissBoxValue.EndToStart -> {
                    network.deleteTime(time.id)
                    true
                }

                SwipeToDismissBoxValue.Settled -> false
            }
        }) { it * 0.8f }
    SwipeToDismissBox(
        dismissState,
        backgroundContent = {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                modifier = Modifier
                    .padding(horizontal = 21.dp)

            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Edit, contentDescription = null)
                    Icon(Icons.Rounded.Delete, contentDescription = null)
                }
            }
        },
        modifier = Modifier.animateContentSize()
    ) {
        TimeItem(time)
    }
}

@Composable
@OptIn(ExperimentalSharedTransitionApi::class)
private fun TimeItem(
    time: Time,
) {
    val nav = LocalNav.current
    val sharedTransition = LocalSharedTransition.current
    val animatedVisibility = LocalAnimated.current

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
            with(sharedTransition) {
                Column {
                    Text(
                        time.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState("${time.id}_title"),
                            animatedVisibility
                        )
                    )
                    Text(
                        time.targetTime.isoTimeFormatter(),
                        fontSize = 12.sp, modifier = Modifier.sharedElement(
                            rememberSharedContentState("${time.id}_targetTime"),
                            animatedVisibility
                        )
                    )
                }
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