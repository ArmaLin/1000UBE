package com.dyaco.spirit_commercial.support

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import com.dyaco.spirit_commercial.R
import timber.log.Timber

/**
 * <uses-permission android:name="android.permission.USE_EXACT_ALARM" tools:ignore="ExactAlarmPolicy" />
 * <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
 * <uses-permission android:name="android.permission.FOREGROUND_SERVICE_SYSTEM_EXEMPTED"/>
 *
 *
<service
android:name=".support.KeepAliveService"
android:enabled="true"
android:exported="false"
android:foregroundServiceType="systemExempted" />

<receiver android:name=".support.KeepAliveService$RestartReceiver"
android:exported="true"
tools:ignore="ExportedReceiver" />
 *
 * MainActivity > KeepAliveService.startService(this);
 */
class KeepAliveService : LifecycleService() {

    companion object {
        private const val CHANNEL_ID = "SystemCoreService"
        private const val CHANNEL_NAME = "Core Process"
        private const val NOTIFICATION_ID = 1001
        private const val TAG = "KeepAliveService"

        @JvmStatic
        fun startService(context: Context) {
            val startIntent = Intent(context, KeepAliveService::class.java)
            try {
                context.startForegroundService(startIntent)
            } catch (e: Exception) {
                Timber.tag(TAG).e("啟動服務異常: ${e.message}")
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.tag(TAG).d("onCreate: 系統保活服務啟動")

        // 1. 啟動前台服務通知 (必須最先執行)
        startForegroundProtection()

        // 2. 使用 RootTools 將本進程設為「不可殺死」 (-1000)
        Thread {
            val success = RootTools.setSelfOomScore(-1000)
            if (success) {
                Timber.tag(TAG).i("已透過 RootTools 鎖定 OOM 權重為 -1000 (System Native)")
            } else {
                Timber.tag(TAG).e("鎖定 OOM 權重失敗，請檢查 Root 權限")
            }
        }.start()

        // 3. 確保加入電池白名單 (利用 RootTools 現有功能)
        RootTools.autoEnsureBatteryWhitelist(packageName)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        // START_STICKY: 如果系統因資源不足殺掉 Service，資源釋放後會自動重建 Service
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.tag(TAG).w("警告: KeepAliveService 被銷毀，嘗試自我重啟...")

        // 發送廣播重啟自己 (雙重保險)
        val restartIntent = Intent(this, RestartReceiver::class.java)
        sendBroadcast(restartIntent)
    }

    private fun startForegroundProtection() {
        try {
            val notification = createNotification()
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                // 使用 systemExempted 因為你是 System App 且有宣告該權限
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED
            )
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "startForeground 失敗")
        }
    }

    private fun createNotification(): Notification {
        val manager = getSystemService(NotificationManager::class.java)

        if (manager != null && manager.getNotificationChannel(CHANNEL_ID) == null) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = "Ensure system stability"
                setShowBadge(false)
                enableVibration(false)
                setSound(null, null)
            }
            manager.createNotificationChannel(serviceChannel)
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(null) // 留空讓 UI 更乾淨
            .setContentText("System Service Running")
            .setSmallIcon(R.mipmap.ic_launcher) // 確保 icon 存在
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    /**
     * 靜態廣播接收器，用於重啟 Service
     * 記得在 AndroidManifest.xml 註冊這個 Receiver
     */
    class RestartReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Timber.tag(TAG).d("收到重啟廣播，正在復活 KeepAliveService...")
            startService(context)
        }
    }
}