package com.dyaco.spirit_commercial.support

import android.content.Context
import android.content.SharedPreferences
import android.os.SystemClock

/**
 * 判斷是不是開機後第一次開啟APP，APP重啟不算
 */
object BootLaunchChecker {

    private const val PREF_NAME = "boot_prefs"
    private const val KEY_LAST_LAUNCH_TIME = "last_launch_time"

    @JvmStatic
    fun isFirstLaunchSinceBoot(context: Context): Boolean {
        return try {
            val prefs: SharedPreferences =
                context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

            val bootTime = SystemClock.elapsedRealtime()
            val lastLaunchTime = prefs.getLong(KEY_LAST_LAUNCH_TIME, -1L)

            val isFirst = (lastLaunchTime == -1L || lastLaunchTime > bootTime)

            prefs.edit().putLong(KEY_LAST_LAUNCH_TIME, bootTime).apply()

            isFirst
        } catch (e: Exception) {
            false
        }
    }
}