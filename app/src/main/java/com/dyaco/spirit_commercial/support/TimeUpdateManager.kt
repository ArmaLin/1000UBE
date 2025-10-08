package com.dyaco.spirit_commercial.support

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dyaco.spirit_commercial.App
import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef
import java.text.SimpleDateFormat
import java.util.*

object TimeUpdateManager {

    // 使用 MutableLiveData 作為內部更新來源，提供不變的 LiveData 給外部觀察
    private val _timeText = MutableLiveData<String>()
    @JvmStatic
    val timeText: LiveData<String> get() = _timeText

    // 單一 BroadcastReceiver 實體，避免多次註冊
    private var receiver: BroadcastReceiver? = null

    @JvmStatic
    fun register(context: Context) {
        // 若已註冊則直接返回
        if (receiver != null) return

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                _timeText.value = updateTime()
            }
        }

        val filter = IntentFilter(Intent.ACTION_TIME_TICK)
        // 使用 applicationContext 可避免因 Activity context 長期存在而導致記憶體洩漏
        context.applicationContext.registerReceiver(receiver, filter)

        // 初始化設定當下時間
        _timeText.value = updateTime()
    }

    @JvmStatic
    fun unregister(context: Context) {
        receiver?.let { reg ->
            // 確保以同一個 applicationContext 取消註冊
            context.applicationContext.unregisterReceiver(reg)
            receiver = null
        }
    }

    @JvmStatic
    fun updateTime(): String {
        val pattern = if (App.TIME_UNIT_E == DeviceIntDef.TF_24HR) "HH:mm" else "hh:mm a"
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(Calendar.getInstance().time)
    }
}
