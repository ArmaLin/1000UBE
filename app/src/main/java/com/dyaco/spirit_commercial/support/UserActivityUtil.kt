package com.dyaco.spirit_commercial.support

import android.content.Context
import android.os.PowerManager
import android.os.SystemClock
import timber.log.Timber
import java.lang.reflect.Method

object UserActivityUtil {

    @Volatile
    private var userActivityMethod: Method? = null

    private const val EVENT_TYPE_TOUCH = 2

    @JvmStatic
    fun pokeUserActivity(context: Context) {
        try {
            if (userActivityMethod == null) {

                synchronized(this) {

                    if (userActivityMethod == null) {
                        Timber.tag("UserActivityUtil").d("第一次初始化 (Get Method)...")

                        userActivityMethod = PowerManager::class.java.getDeclaredMethod(
                            "userActivity",
                            Long::class.javaPrimitiveType,
                            Int::class.javaPrimitiveType,
                            Int::class.javaPrimitiveType
                        ).apply {
                            isAccessible = true
                        }
                    }
                }
            }

            val pm = context.applicationContext.getSystemService(Context.POWER_SERVICE) as? PowerManager
            if (pm == null) {
                Timber.tag("UserActivityUtil").e("Poke failed: PowerManager is null.")
                return
            }

            userActivityMethod?.invoke(
                pm,
                SystemClock.uptimeMillis(),
                EVENT_TYPE_TOUCH,
                0
            )

        } catch (e: Exception) {
            Timber.tag("UserActivityUtil").e(e, "Poke failed (using reflection):")

            synchronized(this) {
                userActivityMethod = null
            }
        }
    }
}