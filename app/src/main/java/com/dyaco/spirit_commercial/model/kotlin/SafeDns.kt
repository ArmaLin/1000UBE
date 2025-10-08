package com.dyaco.spirit_commercial.model.kotlin

import okhttp3.Dns
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.concurrent.*

class SafeDns(private val timeoutInSeconds: Long = 5) : Dns {
    // 使用 cachedThreadPool 共用執行緒，設定為 daemon 以免阻止 JVM 結束
    private val executor: ExecutorService = Executors.newCachedThreadPool { runnable ->
        Thread(runnable).apply { isDaemon = true }
    }

    override fun lookup(hostname: String): List<InetAddress> {
        val future = executor.submit<List<InetAddress>> {
            InetAddress.getAllByName(hostname).toList()
        }
        return try {
            future.get(timeoutInSeconds, TimeUnit.SECONDS)
        } catch (e: TimeoutException) {
            future.cancel(true) // 逾時後取消任務
            fallbackLookup(hostname, e)
        } catch (e: Exception) {
            fallbackLookup(hostname, e)
        }
    }

    private fun fallbackLookup(hostname: String, cause: Exception): List<InetAddress> {
        return try {
            Dns.SYSTEM.lookup(hostname)
        } catch (fallbackEx: Exception) {
            throw UnknownHostException("DNS lookup failed for $hostname").apply {
                initCause(fallbackEx)
            }
        }
    }
}
