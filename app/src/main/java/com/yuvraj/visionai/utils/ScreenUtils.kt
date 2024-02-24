package com.yuvraj.visionai.utils

import android.app.Activity
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import java.math.BigDecimal
import java.math.RoundingMode

object ScreenUtils {
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
        val d = Math.sqrt((realWidth / metrics.xdpi * (realWidth / metrics.xdpi) + realHeight / metrics.ydpi * (realHeight / metrics.ydpi)).toDouble())
        mInch = BigDecimal(d).setScale(1, RoundingMode.HALF_UP).toDouble()
        return mInch
    }


    fun hideStatusBar(activity: Activity) {
        // log the current version of the device
        Log.d("DEBUG", "Device Version API: " + Build.VERSION.SDK_INT )
        Log.d("DEBUG", "Device Version CODE: " + Build.VERSION.CODENAME )

        if (Build.VERSION.SDK_INT < 16) {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R){
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            activity.actionBar?.hide()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.window.setDecorFitsSystemWindows(false)
        }
    }
}