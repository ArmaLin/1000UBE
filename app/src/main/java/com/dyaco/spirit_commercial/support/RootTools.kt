@file:JvmName("RootTools")

package com.dyaco.spirit_commercial.support

import androidx.annotation.Keep
import androidx.core.text.isDigitsOnly
import com.dyaco.spirit_commercial.App
import timber.log.Timber

/**
 * 一個集中管理所有需要 root 權限操作的工具類。
 * 包含了系統欄控制、裝置控制、應用程式管理等功能。
 * 使用 object 關鍵字使其成為一個單例，方便在任何地方直接呼叫，例如 RootTools.rebootDevice()。
 */
@Keep
object RootTools {

    private const val TAG = "RootTools"

    /**
     * 執行單一指令的輔助方法。
     * @param command 要執行的 Shell 指令。
     * @param actionName 用於日誌記錄的操作名稱。
     * @return 指令是否成功執行。
     */
    private fun execute(command: String, actionName: String): Boolean {
        val result = executeAsRoot(command)
        return if (result.isSuccessful()) {
            Timber.tag(TAG).i("Successfully executed: $actionName")
            true
        } else {
            Timber.tag(TAG).e("Failed to $actionName. Exit code: ${result.exitCode}, Error: ${result.error}")
            false
        }
    }

    /**
     * 執行多個指令的輔助方法。
     * @param commands 要執行的 Shell 指令序列。
     * @param actionName 用於日誌記錄的操作名稱。
     * @return 所有指令是否都成功執行。
     */
    private fun execute(vararg commands: String, actionName: String): Boolean {
        val result = executeAsRoot(*commands)
        return if (result.isSuccessful()) {
            Timber.tag(TAG).i("Successfully executed: $actionName")
            true
        } else {
            Timber.tag(TAG).e("Failed to $actionName. Exit code: ${result.exitCode}, Error: ${result.error}")
            false
        }
    }

    /**
     * 執行單一指令的輔助方法。
     * @param command 要執行的 Shell 指令。
     * @param actionName 用於日誌記錄的操作名稱。
     * @return [Pair.first] 表示所有指令是否都成功執行；[Pair.second] 表示取到的 [CommandResult]。
     */
    private fun executeGetResult(
        command: String,
        actionName: String,
    ): Pair<Boolean, CommandResult> {
        val result = executeAsRoot(command)
        return if (result.isSuccessful()) {
            Timber.tag(TAG).i("executeGetResult Successfully executed: $actionName")
            Pair(true, result)
        } else {
            Timber.tag(TAG)
                .e("executeGetResult Failed to $actionName. Exit code: ${result.exitCode}, Error: ${result.error}")
            Pair(false, result)
        }
    }

    /**
     * 執行多個指令的輔助方法。
     * @param commands 要執行的 Shell 指令序列。
     * @param actionName 用於日誌記錄的操作名稱。
     * @return [Pair.first] 表示所有指令是否都成功執行；[Pair.second] 表示取到的 [CommandResult]。
     */
    private fun executeGetResult(
        vararg commands: String,
        actionName: String,
    ): Pair<Boolean, CommandResult> {
        val result = executeAsRoot(*commands)
        return if (result.isSuccessful()) {
            Timber.tag(TAG).i("executeGetResult Successfully executed: $actionName")
            Pair(true, result)
        } else {
            Timber.tag(TAG)
                .e("executeGetResult Failed to $actionName. Exit code: ${result.exitCode}, Error: ${result.error}")
            Pair(false, result)
        }
    }



    private const val CMD_SHOW_STATUS_BAR = "am broadcast -a com.systemui.statusbar.show"
    private const val CMD_HIDE_STATUS_BAR = "am broadcast -a com.systemui.statusbar.hide"
    private const val CMD_SHOW_NAVIGATION_BAR = "am broadcast -a com.systemui.navigationbar.show"
    private const val CMD_HIDE_NAVIGATION_BAR = "am broadcast -a com.systemui.navigationbar.hide"

    private const val CMD_BIND_USB_2_HUB = "echo 1 > sys/bus/usb/devices/usb1/authorized"
    private const val CMD_UNBIND_USB_2_HUB = "echo 0 > sys/bus/usb/devices/usb1/authorized"

    @JvmStatic
    fun showStatusBar(): Boolean = execute(CMD_SHOW_STATUS_BAR, "show status bar")

    @JvmStatic
    fun hideStatusBar(): Boolean = execute(CMD_HIDE_STATUS_BAR, "hide status bar")

    @JvmStatic
    fun showNavigationBar(): Boolean = execute(CMD_SHOW_NAVIGATION_BAR, "show navigation bar")

    @JvmStatic
    fun hideNavigationBar(): Boolean = execute(CMD_HIDE_NAVIGATION_BAR, "hide navigation bar")


    // --- Device Control ---

    @JvmStatic
    fun rebootDevice(): Boolean = execute("reboot", "reboot device")

    @JvmStatic
    fun shutdownDevice(): Boolean = execute("reboot -p", "shutdown device")


    // --- App Management ---

    @JvmStatic
    fun forceStopApp(packageName: String): Boolean = execute("am force-stop $packageName", "force stop app '$packageName'")

    @JvmStatic
    fun clearAppData(packageName: String): Boolean = execute("pm clear $packageName", "clear data for app '$packageName'")

    @JvmStatic
    fun silentInstallApp(apkPath: String): Boolean = execute("pm install -r $apkPath", "silent install app from '$apkPath'")



    @JvmStatic
    fun simulateTap(x: Int, y: Int): Boolean = execute("input tap $x $y", "simulate tap at ($x, $y)")



    @JvmStatic
    fun setAirplaneMode(enable: Boolean): Boolean {
        val value = if (enable) 1 else 0
        val state = if (enable) "ON" else "OFF"
        val command1 = "settings put global airplane_mode_on $value"
        val command2 = "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state $enable"

        return execute(command1, command2, actionName = "turn airplane mode $state")
    }



    /**
     * 模擬按下指定的 Key Code。
     * @param keyCode 要模擬的按鍵碼 (例如 26 代表電源鍵)。
     * @return 指令是否成功執行。
     */
    @JvmStatic
    fun simulateKeyEvent(keyCode: Int): Boolean = execute("input keyevent $keyCode", "simulate key event '$keyCode'")

    /**
     * 模擬按下電源鍵 (KEYCODE_POWER = 26)。
     * 這是 simulateKeyEvent(26) 的一個便捷方法。
     * @return 指令是否成功執行。
     */
    @JvmStatic
    fun simulatePowerKeyPress(): Boolean = simulateKeyEvent(26)

    /**
     * 綁定 USB 2.0  HUB。
     *
     * 解綁目的是避免 HDMI 聲音撥出來
     */
    @JvmStatic
    fun bindUsb2Hub(): Boolean = execute(CMD_BIND_USB_2_HUB, "bind USB 2.0 Hub")

    /**
     * 解除綁定 USB 2.0 HUB。
     *
     * 解綁目的是避免 HDMI 聲音撥出來
     */
    @JvmStatic
    fun unbindUsb2Hub(): Boolean = execute(CMD_UNBIND_USB_2_HUB, "unbind USB 2.0 Hub")

    /**
     * 設置默認的啟動器Launcher
     * @param packageName 包名
     * @param activityLauncherClassPath Launcher activity 的全名，例如 com.example.MainActivity
     */
    @JvmStatic
    fun setDefaultHomeLauncher(packageName: String, activityLauncherClassPath: String): Boolean =
        execute(
            "cmd package set-home-activity $packageName/$activityLauncherClassPath",
            "set default home launcher packageName=$packageName, activityLauncherClassPath=$activityLauncherClassPath"
        )


    /**
     * 設置默認的啟動器Launcher
     * @param packageName 包名
     * @param activityLauncherClassType Launcher activity 的 Class
     */
    @JvmStatic
    fun setDefaultHomeLauncher(packageName: String, activityLauncherClassType: Class<*>): Boolean =
        setDefaultHomeLauncher(packageName, activityLauncherClassType.name)

    /**
     * 取得 App 的 Activity Task Id
     */
    @JvmStatic
    fun getAppActivitiesActivityTaskIdList(packageName: String): List<String> {
        try {
            val pair01 = executeGetResult(
                "dumpsys activity activities | grep $packageName | grep 'Hist  #'",
                "找 $packageName 的 taskId"
            )
            Timber.d("getAppActivitiesActivityTaskIdList  packageName=$packageName\npair01=$pair01")

            if (!pair01.first) {
                Timber.w("getAppActivitiesActivityTaskIdList packageName=$packageName 找 taskId 失敗")
                return emptyList()
            }

            val result = pair01.second.output

            if (result.isEmpty()) {
                Timber.w("getAppActivitiesActivityTaskIdList packageName=$packageName 沒有回傳資料")
                return emptyList()
            }

            val outputList = result.split("\n")

            val taskIdList: MutableList<String> = mutableListOf()

            for (output in outputList) {
                Timber.d("getAppActivitiesActivityTaskIdList packageName=$packageName\noutput=$output")
                // 取得範例
                // Hist  #0: ActivityRecord{e42b81d u0 com.android.settings/.homepage.SettingsHomepageActivity t15778}
                val regex = Regex("t(\\d+)")
                val taskId = try {
                    regex.find(output)?.groupValues[1]
                } catch (e: Exception) {
                    Timber.e(e)
                    null
                }

                if (taskId.isNullOrEmpty()) {
                    continue
                }

                if (!taskId.isDigitsOnly()) {
                    continue
                }

                if (taskIdList.contains(taskId)) {
                    continue
                }

                taskIdList.add(taskId)
            }

            return taskIdList
        } catch (e: Exception) {
            Timber.e(e)
            return emptyList()
        }
    }

    /**
     * 關閉 App 的 Activity Task
     * 不殺死 App
     * 只關閉畫面，不影響進程
     * @param packageName 包名
     */
    @JvmStatic
    fun closeAppActivitiesActivityTaskWithShell(packageName: String) {
        try {
            val myAppTaskIdList = getAppActivitiesActivityTaskIdList(App.getApp().packageName)

            Timber.d("closeAppActivitiesActivityTaskWithShell myAppTaskIdList=$myAppTaskIdList")

            val appTaskIdList = getAppActivitiesActivityTaskIdList(packageName)

            Timber.d("closeAppActivitiesActivityTaskWithShell appTaskIdList=$appTaskIdList")

            for (appTaskId in appTaskIdList) {
                Timber.d("closeAppActivitiesActivityTaskWithShell packageName=$packageName appTaskId=$appTaskId")

                if (myAppTaskIdList.contains(appTaskId)) {
                    // 避免清掉自己 app TaskId
                    continue
                }

                val pair = executeGetResult(
                    "am stack remove $appTaskId",
                    "$packageName 移除這個 taskId=$appTaskId"
                )
                Timber.d("closeAppActivitiesActivityTaskWithShell packageName=$packageName\npair=$pair")
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}