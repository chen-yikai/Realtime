package dev.eliaschen.realtime.network

import android.util.Log
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class SocketListener() : WebSocketListener() {
    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.i("socket", text)
    }
}