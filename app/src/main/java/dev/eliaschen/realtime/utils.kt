package dev.eliaschen.realtime

import dev.eliaschen.realtime.network.Countdown
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit

fun String.isoTimeFormatter(
    pattern: String = "yyyy/MM/dd hh:mm a",
): String {
    val date = OffsetDateTime.parse(this).atZoneSameInstant(ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.TAIWAN)
    return date.format(formatter)
}

fun String.timeLeft(): Countdown {
    val target = Instant.parse(this).toEpochMilli()
    val current = System.currentTimeMillis()
    val left = target - current
    val years = TimeUnit.MILLISECONDS.toDays(left) / 360
    val days = TimeUnit.MILLISECONDS.toDays(left)
    val hours = TimeUnit.MILLISECONDS.toHours(left) % 24
    val minutes = TimeUnit.MILLISECONDS.toMinutes(left) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(left) % 60

    return Countdown(
        year = years.toInt(),
        day = days.toInt(),
        hour = hours.toInt(),
        minute = minutes.toInt(),
        second = seconds.toInt()
    )
}

fun Long.formatMillis(pattern: String): String {
    val formatter = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault())
    return formatter.format(Instant.ofEpochMilli(this))
}