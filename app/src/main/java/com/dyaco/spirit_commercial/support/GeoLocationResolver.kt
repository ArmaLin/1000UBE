package com.dyaco.spirit_commercial.support

import android.util.Log
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject

interface GeoLocationCallback {
    fun onSuccess(value: String)
    fun onFailure(error: String)
}

private const val TAG = "GeoLocationResolver"

class GeoLocationResolver private constructor(
    private val requests: List<GeoRequest>,
    private val ordered: Boolean
) {
    data class GeoRequest(val url: String, val keyPath: String)

    companion object {
        // 單例 OkHttpClient
        private val client = OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .build()

        @JvmStatic
        fun newRequest(): Builder = Builder()
    }

    class Builder {
        private val reqs = mutableListOf<GeoRequest>()
        private var ordered = false

        fun add(url: String, keyPath: String) = apply {
            reqs += GeoRequest(url, keyPath)
        }

        fun ordered(flag: Boolean) = apply {
            this.ordered = flag
        }

        fun execute(callback: GeoLocationCallback) {
            val resolver = GeoLocationResolver(reqs.toList(), ordered)
            resolver.start(callback)
        }
    }

    private fun start(callback: GeoLocationCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            val list = if (ordered) requests else requests.shuffled()
            val result = resolveAll(list)
            withContext(Dispatchers.Main) {
                if (result != null) {
                    callback.onSuccess(result)
                } else {
                    callback.onFailure("All URLs failed or the specified field could not be found.")
                }
            }
        }
    }

    private suspend fun resolveAll(list: List<GeoRequest>): String? {
        // 一開始 log 出目前的請求順序
        Log.d(TAG, "🔀 請求順序（${if (ordered) "有序" else "隨機"}）:")
        list.forEachIndexed { idx, req ->
            Log.d(TAG, "  ${idx + 1}/${list.size} → ${req.url} [keyPath=${req.keyPath}]")
        }

        for ((idx, req) in list.withIndex()) {
            val step = idx + 1
            Log.d(TAG, "🌐 [${step}/${list.size}] 嘗試取 ${req.url}")
            // 1. 先抓原始字串
            val text = fetch(req.url)
            if (text == null) {
                Log.d(TAG, "❌ [${step}/${list.size}] ${req.url} fetch 失敗，returned null")
                continue
            }
            // 2. 嘗試解析為 JSONObject
            val json = runCatching { JSONObject(text) }
                .onFailure { e ->
                    Log.d(TAG, "❌ [${step}/${list.size}] ${req.url} JSON 解析失敗：${e.message}")
                }
                .getOrNull() ?: continue

            // 3. 按 keyPath 抽值
            extract(json, req.keyPath)?.let { value ->
                Log.d(TAG, "✅ 從 ${req.url} 取得 ${req.keyPath} = $value")
                return value
            }
            Log.d(TAG, "⚠️ [${step}/${list.size}] ${req.url} 無法解析 ${req.keyPath}")
        }
        return null
    }

    private suspend fun fetch(url: String): String? = withContext(Dispatchers.IO) {
        try {
            val req = Request.Builder()
                .url(url)
                .get()
                .addHeader("User-Agent", "Mozilla/5.0 (Android GeoLocationResolver)")
                .build()
            client.newCall(req).execute().use { resp ->
                return@withContext if (resp.isSuccessful) {
                    resp.body?.string()
                } else {
                    Log.d(TAG, "❌ $url 回應碼：${resp.code}")
                    null
                }
            }
        } catch (e: Exception) {
            return@withContext null
        }
    }

    /**
     * 支援 "key", "key[2]", "parent.child[0].foo" 等格式
     */
    private fun extract(node: Any, path: String): String? {
        var current: Any? = node
        val idxRx = Regex("^(.+)\\[(\\d+)]$")
        for (seg in path.split('.')) {
            if (current == null) return null
            current = when {
                current is JSONObject && idxRx.matches(seg) -> {
                    val (key, idx) = idxRx.find(seg)!!.destructured
                    current.optJSONArray(key)?.opt(idx.toInt())
                }
                current is JSONObject -> current.opt(seg)
                current is JSONArray && seg.toIntOrNull() != null ->
                    current.opt(seg.toInt())
                else -> null
            }
        }
        return when (current) {
            is String -> current.takeIf { it.isNotBlank() }
            is Number, is Boolean -> current.toString()
            is JSONArray -> current.optString(0).takeIf { it.isNotBlank() }
            else -> null
        }
    }
}
