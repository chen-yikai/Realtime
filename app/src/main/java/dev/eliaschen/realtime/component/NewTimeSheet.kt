package dev.eliaschen.realtime.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.eliaschen.realtime.LocalNetwork
import dev.eliaschen.realtime.formatMillis
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTimeSheet(editId: String, modifier: Modifier = Modifier, onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    val network = LocalNetwork.current
    val title = rememberTextFieldState()
    var date by remember {
        mutableLongStateOf(
            LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
    }
    var time by remember { mutableLongStateOf(0L) }
    val datetime = date + time
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val isEditMode = editId.isNotEmpty()

    LaunchedEffect(isEditMode) {
        if (!isEditMode) return@LaunchedEffect
        network.times.first { it.id == editId }.let {
            title.edit {
                replace(0, length, it.title)
            }
            val localDateTime = OffsetDateTime.parse(it.targetTime)
                .atZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime()
            date = localDateTime.toLocalDate()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
            time = (localDateTime.hour * 3600L + localDateTime.minute * 60L) * 1000L
        }
    }

    if (showDatePicker) {
        CustomDatePicker(date, onDismiss = { showDatePicker = false }, onSubmit = { date = it })
    }
    if (showTimePicker) {
        CustomTimePicker(time, onDismiss = { showTimePicker = false }, onSubmit = { time = it })
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .imePadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                if (isEditMode) "Edit time" else "Create new time",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            BasicTextField(
                title,
                lineLimits = TextFieldLineLimits.SingleLine,
                textStyle = MaterialTheme.typography.bodyLarge, decorator = {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomStart) {
                        it()
                        if (title.text.isEmpty()) {
                            Text("Title")
                        }
                        HorizontalDivider()
                    }
                }
            )
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                CustomInputButton(
                    datetime.formatMillis("yyyy/MM/dd"),
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1f)
                )
                CustomInputButton(
                    datetime.formatMillis("hh:mm a"),
                    onClick = { showTimePicker = true },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(10.dp))
            Button(onClick = {
                scope.launch {
                    if (isEditMode) {
                        network.updateTime(editId, title.text.toString(), datetime)
                    } else {
                        network.createNewTime(title.text.toString(), datetime)
                    }
                    onDismiss()
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text(if (isEditMode) "Edit" else "Create")
            }
        }
    }
}

@Composable
private fun CustomInputButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick),
    ) {
        Text(label)
        HorizontalDivider()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTimePicker(
    value: Long,
    onDismiss: () -> Unit,
    onSubmit: (Long) -> Unit, modifier: Modifier = Modifier
) {
    val time = rememberTimePickerState(
        initialHour = TimeUnit.MILLISECONDS.toHours(value).toInt(),
        initialMinute = (TimeUnit.MILLISECONDS.toMinutes(value) % 60).toInt()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } },
        confirmButton = {
            OutlinedButton(onClick = {
                val totalMillis = (time.hour * 3600L + time.minute * 60L) * 1000L
                onSubmit(totalMillis)
                onDismiss()
            }) { Text("Confirm") }
        }) {
        Box(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            TimePicker(time)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomDatePicker(
    value: Long,
    onDismiss: () -> Unit,
    onSubmit: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val utcDate = Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toLocalDate()
        .atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    val date = rememberDatePickerState(
        initialSelectedDateMillis = utcDate
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } },
        confirmButton = {
            OutlinedButton(
                onClick = {
                    date.selectedDateMillis?.let { utcMillis ->
                        val localMidnight = Instant.ofEpochMilli(utcMillis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli()
                        onSubmit(localMidnight)
                        onDismiss()
                    }
                },
            ) { Text("Confirm") }
        }) {
        DatePicker(date)
    }
}
