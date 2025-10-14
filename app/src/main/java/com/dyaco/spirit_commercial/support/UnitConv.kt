@file:JvmName("UnitConv")

package com.dyaco.spirit_commercial.support

import com.dyaco.spirit_commercial.support.intdef.DeviceIntDef.IMPERIAL
import kotlin.math.pow
import kotlin.math.round

object UnitConv {

    private const val KM_MI = 0.621371
    private const val M_FT = 3.28084
    private const val KG_LB = 2.20462
    private const val CM_IN = 0.393701
    // 1 英尺等於 30.48 公分
    private const val FT_CM = 30.48

    @JvmStatic
    fun roundTo(v: Double, digits: Int): Double {
        val factor = 10.0.pow(digits)
        return round(v * factor) / factor
    }

    @JvmStatic
    fun roundTo(v: Float, digits: Int): Float {
        val factor = 10f.pow(digits)
        return round(v * factor) / factor
    }

    // 公里 ↔ 英里
    @JvmStatic
    fun kmToMi(v: Float): Float = v * KM_MI.toFloat()
    @JvmStatic
    fun kmToMi(v: Float, digits: Int): Float = roundTo(kmToMi(v), digits)
    @JvmStatic
    fun kmToMi(v: Double): Double = v * KM_MI
    @JvmStatic
    fun kmToMi(v: Double, digits: Int): Double = roundTo(kmToMi(v), digits)

    @JvmStatic
    fun miToKm(v: Float): Float = v / KM_MI.toFloat()
    @JvmStatic
    fun miToKm(v: Float, digits: Int): Float = roundTo(miToKm(v), digits)
    @JvmStatic
    fun miToKm(v: Double): Double = v / KM_MI
    @JvmStatic
    fun miToKm(v: Double, digits: Int): Double = roundTo(miToKm(v), digits)

    // 公尺 ↔ 英尺
    @JvmStatic
    fun mToFt(v: Float): Float = v * M_FT.toFloat()
    @JvmStatic
    fun mToFt(v: Float, digits: Int): Float = roundTo(mToFt(v), digits)
    @JvmStatic
    fun mToFt(v: Double): Double = v * M_FT
    @JvmStatic
    fun mToFt(v: Double, digits: Int): Double = roundTo(mToFt(v), digits)

    @JvmStatic
    fun ftToM(v: Float): Float = v / M_FT.toFloat()
    @JvmStatic
    fun ftToM(v: Float, digits: Int): Float = roundTo(ftToM(v), digits)
    @JvmStatic
    fun ftToM(v: Double): Double = v / M_FT
    @JvmStatic
    fun ftToM(v: Double, digits: Int): Double = roundTo(ftToM(v), digits)

    // 體重
    @JvmStatic
    fun kgToLb(v: Float): Float = v * KG_LB.toFloat()
    @JvmStatic
    fun kgToLb(v: Float, digits: Int): Float = roundTo(kgToLb(v), digits)
    @JvmStatic
    fun kgToLb(v: Double): Double = v * KG_LB
    @JvmStatic
    fun kgToLb(v: Double, digits: Int): Double = roundTo(kgToLb(v), digits)

    @JvmStatic
    fun lbToKg(v: Float): Float = v / KG_LB.toFloat()
    @JvmStatic
    fun lbToKg(v: Float, digits: Int): Float = roundTo(lbToKg(v), digits)
    @JvmStatic
    fun lbToKg(v: Double): Double = v / KG_LB
    @JvmStatic
    fun lbToKg(v: Double, digits: Int): Double = roundTo(lbToKg(v), digits)

    // 身高 (公分 ↔ 英寸)
    @JvmStatic
    fun cmToIn(v: Float): Float = v * CM_IN.toFloat()
    @JvmStatic
    fun cmToIn(v: Float, digits: Int): Float = roundTo(cmToIn(v), digits)
    @JvmStatic
    fun cmToIn(v: Double): Double = v * CM_IN
    @JvmStatic
    fun cmToIn(v: Double, digits: Int): Double = roundTo(cmToIn(v), digits)

    @JvmStatic
    fun inToCm(v: Float): Float = v / CM_IN.toFloat()
    @JvmStatic
    fun inToCm(v: Float, digits: Int): Float = roundTo(inToCm(v), digits)
    @JvmStatic
    fun inToCm(v: Double): Double = v / CM_IN
    @JvmStatic
    fun inToCm(v: Double, digits: Int): Double = roundTo(inToCm(v), digits)

    // 身高 (公分 ↔ 英尺)
    // -----------------------------------
    @JvmStatic
    fun cmToFt(v: Float): Float = (v / FT_CM).toFloat()
    @JvmStatic
    fun cmToFt(v: Float, digits: Int): Float = roundTo(cmToFt(v), digits)
    @JvmStatic
    fun cmToFt(v: Double): Double = v / FT_CM
    @JvmStatic
    fun cmToFt(v: Double, digits: Int): Double = roundTo(cmToFt(v), digits)

    @JvmStatic
    fun ftToCm(v: Float): Float = (v * FT_CM).toFloat()
    @JvmStatic
    fun ftToCm(v: Float, digits: Int): Float = roundTo(ftToCm(v), digits)
    @JvmStatic
    fun ftToCm(v: Double): Double = v * FT_CM
    @JvmStatic
    fun ftToCm(v: Double, digits: Int): Double = roundTo(ftToCm(v), digits)
    // 💡 --- 結束 ---


    @JvmStatic
    fun dpsToRpm(dps: Int): Int = dps / 6

    // -----------------------------------
    // 公尺 ↔ 公里
    // -----------------------------------
    @JvmStatic
    fun mToKm(v: Float): Float = v / 1_000f
    @JvmStatic
    fun mToKm(v: Float, digits: Int): Float = roundTo(mToKm(v), digits)
    @JvmStatic
    fun mToKm(v: Double): Double = v / 1_000.0
    @JvmStatic
    fun mToKm(v: Double, digits: Int): Double = roundTo(mToKm(v), digits)

    @JvmStatic
    fun kmToM(v: Float): Float = v * 1_000f
    @JvmStatic
    fun kmToM(v: Float, digits: Int): Float = roundTo(kmToM(v), digits)
    @JvmStatic
    fun kmToM(v: Double): Double = v * 1_000.0
    @JvmStatic
    fun kmToM(v: Double, digits: Int): Double = roundTo(kmToM(v), digits)


}