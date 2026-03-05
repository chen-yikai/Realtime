package dev.eliaschen.realtime.network

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

val networkJson = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

@Serializable
data class Time(
    val id: String,
    val title: String,
    val targetTime: String,
    val createdAt: String,
)

@Serializable
data class Countdown(
    val year: Int, val day: Int, val hour: Int, val minute: Int, val second: Int,
)