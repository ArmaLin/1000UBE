// ApiResponseListener.kt
package com.dyaco.spirit_commercial.model.kotlin

/**
 * 定義供 Java 呼叫時使用的 Callback 抽象類別
 * 抽象方法 onSuccess(data, httpCode) 可被 Java 覆寫，
 * 輔助方法 onSuccess(data) 會 delegate 呼叫 onSuccess(data, httpCode)，預設 httpCode 為 -1。
 */
abstract class ApiResponseListener<T> {
    // 提供有 httpCode 的 onSuccess，預設 delegate 給只有 data 的版本
    open fun onSuccess(data: T, httpCode: Int) {
        onSuccess(data)
    }

    // 提供只有 data 的 onSuccess，預設為空實作
    open fun onSuccess(data: T) {
        // 預設不做處理
    }

    // 提供有 httpCode 的 onFailure，預設 delegate 給只有 error 的版本
    open fun onFailure(error: Throwable, httpCode: Int?) {
        onFailure(error)
    }

    // 提供只有 error 的 onFailure，預設為空實作
    open fun onFailure(error: Throwable) {
        // 預設不做處理
    }
}