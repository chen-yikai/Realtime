package dev.eliaschen.realtime.network

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

const val host = "https://realtime.skills.eliaschen.dev"

open class NetworkClient(private val context: Application) : AndroidViewModel(context) {
    val client = OkHttpClient()

    protected suspend inline fun <reified T> get(url: String): T = withContext(Dispatchers.IO) {
        val req = Request.Builder().url(host + url).get().build()
        val res = client.newCall(req).execute()
        if (!res.isSuccessful) throw Exception("Failed to get $url")
        val body = res.body?.string() ?: throw Exception("Null body of $url")
        return@withContext networkJson.decodeFromString(body)
    }

    protected suspend inline fun <reified T> post(url: String, payload: String): T =
        withContext(Dispatchers.IO) {
            val req =
                Request.Builder().url(host + url).post(
                    payload.toRequestBody("application/json".toMediaType())
                ).build()
            val res = client.newCall(req).execute()
            if (!res.isSuccessful) throw Exception("${res.code} Failed to post $url")
            val body = res.body?.string() ?: throw Exception("Null body of $url")
            return@withContext networkJson.decodeFromString(body)
        }

    protected suspend fun delete(url: String) = withContext(Dispatchers.IO) {
        val req = Request.Builder().url(host + url).delete().build()
        client.newCall(req).execute().use { res ->
            if (!res.isSuccessful) throw Exception(" ${res.code} Failed to delete $url")
        }
    }

    protected suspend fun patch(url: String, payload: String) = withContext(Dispatchers.IO) {
        val req = Request.Builder().url(host + url)
            .patch(payload.toRequestBody("application/json".toMediaType())).build()
        client.newCall(req).execute().let { res ->
            if (!res.isSuccessful) throw Exception("${res.code} Failed to patch $url")
        }
    }

    protected fun reject(message: String?) {
        Log.e("NetworkClient", message ?: "Error")
        Toast.makeText(context, "NetworkClient: $message", Toast.LENGTH_SHORT).show()
    }
}