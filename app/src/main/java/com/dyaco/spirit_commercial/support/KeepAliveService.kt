package com.dyaco.spirit_commercial.support

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import com.dyaco.spirit_commercial.R

/**
 * <uses-permission android:name="android.permission.USE_EXACT_ALARM" tools:ignore="ExactAlarmPolicy" />
 * <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
 * <uses-permission android:name="android.permission.FOREGROUND_SERVICE_SYSTEM_EXEMPTED"/>
 *
 *
 * <service
 * android:name=".KeepAliveService"
 * android:enabled="true"
 * android:exported="false"
 * android:foregroundServiceType="systemExempted" />
 *
 * MainActivity > KeepAliveService.startService(this);
 */
class KeepAliveService : LifecycleService() {

    companion object {
        private const val CHANNEL_ID = "KeepAliveServiceChannel"
        private const val CHANNEL_NAME = "System Service"
        private const val NOTIFICATION_ID = 1
        private const val TAG = "KeepAliveService"

        @JvmStatic
        fun startService(context: Context) {
            val startIntent = Intent(context, KeepAliveService::class.java)
            try {
                context.startForegroundService(startIntent)
            } catch (e: Exception) {
                Log.e(TAG, "啟動服務失敗:$e")
            }
        }
    }

    @SuppressLint("InlinedApi")
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: 服務建立中...")

        val notification = createLowPriorityNotification()

        try {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED
            )
            Log.d(TAG, "onCreate: startForeground 成功。")
        } catch (e: Exception) {
            // 如果這裡出錯，通常是 Manifest 權限沒設對，或簽章不符
            Log.e(TAG, "onCreate: startForeground 失敗! 請檢查 'systemExempted' 權限和簽章。", e)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d(TAG, "onStartCommand: 服務已啟動 (startId: $startId, flags: $flags)")
        return START_STICKY // 確保服務被殺掉後會重啟
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: 服務被銷毀。")
        super.onDestroy()
    }

    private fun createLowPriorityNotification(): Notification {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        serviceChannel.description = "Keeps the app service running."
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(serviceChannel)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("System Service")
            .setContentText("Service is running to ensure app stability.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }
}