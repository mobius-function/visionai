package com.yuvraj.visionai.utils

import android.app.Activity
import android.graphics.Point
import android.util.DisplayMetrics
import java.math.BigDecimal
import java.math.RoundingMode

object ScreenInchUtils {
    private var mInch = 0.0
    fun getScreenInch(activity: Activity): Double {
        if (mInch != 0.0) {
            return mInch
        }
        var realWidth = 0
        var realHeight = 0
        val display = activity.windowManager.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        val size = Point()
        display.getRealSize(size)
        realWidth = size.x
        realHeight = size.y
        mInch = formatDouble(
            Math.sqrt((realWidth / metrics.xdpi * (realWidth / metrics.xdpi) + realHeight / metrics.ydpi * (realHeight / metrics.ydpi)).toDouble()),
            1
        )
        return mInch
    }

    /**
     * Returns a double value with the specified number of decimal places (rounded using half-up rounding).
     * newScale is the specified number of decimal places.
     * @return
     */
    fun formatDouble(d: Double, newScale: Int): Double {
        val bd = BigDecimal(d)
        return bd.setScale(newScale, RoundingMode.HALF_UP).toDouble()
    }
}