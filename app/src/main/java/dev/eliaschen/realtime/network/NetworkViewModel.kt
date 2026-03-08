package dev.eliaschen.realtime.network

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.time.Instant

class NetworkViewModel(private val context: Application) : NetworkClient(context) {
    val times = mutableStateListOf<Time>()

    init {
        refreshTimes()
    }

    fun refreshTimes() {
        viewModelScope.launch {
            getAllTimes().let {
                times.clear()
                times.addAll(it)
            }
        }
    }

    suspend fun getAllTimes(): List<Time> {
        try {
            return get("/api/countdowns")
        } catch (e: Exception) {
            reject(e.message)
            return emptyList()
        }
    }

    suspend fun createNewTime(title: String, targetTime: Long): Time? {
        val payload = mapOf(
            "title" to title,
            "targetTime" to Instant.ofEpochMilli(targetTime).toString()
        )
        try {
            val response = post<Time>("/api/countdowns", networkJson.encodeToString(payload))
            refreshTimes()
            return response
        } catch (e: Exception) {
            reject(e.message)
            return null
        }
    }

    fun updateTime(id: String, title: String, targetTime: Long) {
        val payload = mapOf(
            "title" to title,
            "targetTime" to Instant.ofEpochMilli(targetTime).toString()
        )
        viewModelScope.launch {
            try {
                patch("/api/countdowns/$id", networkJson.encodeToString(payload))
                refreshTimes()
            } catch (e: Exception) {
                reject(e.message)
            }
        }
    }

    fun deleteTime(id: String) {
        viewModelScope.launch {
            try {
                delete("/api/countdowns/$id")
                refreshTimes()
            } catch (e: Exception) {
                reject(e.message)
            }
        }
    }

    fun getCountdownStream(id: String): Flow<Countdown> = callbackFlow {
        val client = OkHttpClient()
        val req = Request.Builder().url("$host/ws/countdowns/$id").build()
        val socket = client.newWebSocket(req, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.i("socket", text)
                viewModelScope.launch {
                    send(networkJson.decodeFromString(text))
                }
            }
        })
        awaitClose {
            socket.close(1000, "Exit")
        }
    }
}