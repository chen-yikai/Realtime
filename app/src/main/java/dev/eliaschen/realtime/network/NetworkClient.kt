package dev.eliaschen.realtime.network

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

const val host = "https://realtime.skills.eliaschen.dev"

open class NetworkClient(private val context: Application) : AndroidViewModel(context) {
    val client = OkHttpClient()

    suspend inline fun <reified T> get(url: String): T = withContext(Dispatchers.IO) {
        val req = Request.Builder().url(host + url).get().build()
        val res = client.newCall(req).execute()
        if (!res.isSuccessful) throw Exception("Failed to get $url")
        val body = res.body?.string() ?: throw Exception("Null body of $url")
        return@withContext networkJson.decodeFromString(body)
    }

    fun reject(message: String?) {
        Log.e("NetworkClient", message ?: "Error")
        Toast.makeText(context, "NetworkClient: $message", Toast.LENGTH_SHORT).show()
    }
}