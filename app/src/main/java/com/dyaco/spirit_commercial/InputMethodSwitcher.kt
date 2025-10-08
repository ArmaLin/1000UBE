package com.dyaco.spirit_commercial

import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import java.util.*

/**
 * 日文輸入法和Gboard做切換
 */
object InputMethodSwitcher {

    private const val TAG = "IME_SWITCH"

    private const val GBOARD_IME = "com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME"
    private const val GBOARD_PACKAGE = "com.google.android.inputmethod.latin"

    private const val JAPANESE_IME = "com.japanese.keyboard.for.android/com.example.android.softkeyboard.SoftKeyboard"
    private const val JAPANESE_PACKAGE = "com.japanese.keyboard.for.android"

    @JvmStatic
    fun switchInputMethod(context: Context) {
        val resolver: ContentResolver = context.contentResolver
        val locale: String = Locale.getDefault().language // "ja", "en", etc.
        val currentIme = Settings.Secure.getString(resolver, Settings.Secure.DEFAULT_INPUT_METHOD)

        val enabledImesRaw = Settings.Secure.getString(resolver, Settings.Secure.ENABLED_INPUT_METHODS) ?: ""
        val enabledImes = enabledImesRaw.split(":").toMutableSet()

        Log.d(TAG, "當前語系: $locale")
        Log.d(TAG, "當前輸入法: $currentIme")
        Log.d(TAG, "目前已啟用輸入法清單: $enabledImes")

        val isGboardInstalled = isPackageInstalled(context, GBOARD_PACKAGE)
        val isJapaneseImeInstalled = isPackageInstalled(context, JAPANESE_PACKAGE)

        Log.d(TAG, "Gboard 安裝狀態: $isGboardInstalled")
        Log.d(TAG, "Japanese Keyboard 安裝狀態: $isJapaneseImeInstalled")

        if (locale == "ja") {
            if (!isJapaneseImeInstalled) {
                Log.d(TAG, "⚠️ 日文語系但未安裝日文鍵盤，操作取消")
                return
            }

            enabledImes.add(JAPANESE_IME)
       //     enabledImes.remove(GBOARD_IME)
            Settings.Secure.putString(resolver, Settings.Secure.ENABLED_INPUT_METHODS, enabledImes.joinToString(":"))
            Settings.Secure.putString(resolver, Settings.Secure.DEFAULT_INPUT_METHOD, JAPANESE_IME)

            Log.d(TAG, "✅ 已啟用日文鍵盤並設定為預設，禁用 Gboard")
        } else {
            if (!isGboardInstalled) {
                Log.d(TAG, "⚠️ 非日語系但未安裝 Gboard，操作取消")
                return
            }

            enabledImes.add(GBOARD_IME)
            enabledImes.remove(JAPANESE_IME)
            Settings.Secure.putString(resolver, Settings.Secure.ENABLED_INPUT_METHODS, enabledImes.joinToString(":"))
            Settings.Secure.putString(resolver, Settings.Secure.DEFAULT_INPUT_METHOD, GBOARD_IME)

            Log.d(TAG, "✅ 已啟用 Gboard 並設定為預設，禁用日文鍵盤")
        }
    }

    private fun isPackageInstalled(context: Context, pkg: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(pkg, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}
