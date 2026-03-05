package dev.eliaschen.realtime.network

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class NetworkViewModel(private val context: Application) : NetworkClient(context) {
    val times = mutableStateListOf<Time>()

    init {
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

    suspend fun getCountdownStream(id: String): Flow<Countdown> = withContext(
        Dispatchers.IO
    ) {
        return@withContext callbackFlow {
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
}