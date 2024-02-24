package com.yuvraj.visionai.utils.helpers

import android.app.Activity
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
import com.yuvraj.visionai.utils.ScreenUtils

class DistanceHelper {

    companion object {
        fun focalLengthFinder(
            measuredDistance: Double,
            realWidth: Double,
            widthInRfImage: Double
        ): Double {
            return (widthInRfImage * measuredDistance) / realWidth
        }

        fun distanceFinder(
            focalLength : Double,
            realFaceWidth : Double,
            faceWidthInFrame : Double): Double {
            return (realFaceWidth * focalLength) / faceWidthInFrame
        }

        fun cmToInch(cm: Double): Double {
            return cm * 0.393701
        }

        fun inchToCm(inch: Double): Double {
            return inch * 2.54
        }

        fun meterToFeet(meter: Double): Double {
            return meter * 3.28084
        }

        fun mmToPixels(mm: Float, activity: Activity?): Float {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, mm,
                activity?.resources?.getDisplayMetrics());
        }

        fun pixelsToDp(px: Int): Float {
            return px / (Resources.getSystem().displayMetrics.densityDpi.toFloat() /
                    DisplayMetrics.DENSITY_DEFAULT)
        }

        fun cmToPixels(cm: Float, activity: Activity?): Int {
            val inch: Float = cm * 10 / 25.4f
            return (inch * getDPI(activity)).toInt()
        }

        fun getDPI(activity: Activity?): Float {
            //get screen size
//            val screenSize: Double = ScreenUtils.getScreenInch(activity)
            val screenSize: Double = ScreenUtils.getScreenInch(activity!!)

            //Get Width and height
            val widthPx: Int = Resources.getSystem().displayMetrics.widthPixels
            val heightPx: Int = Resources.getSystem().displayMetrics.heightPixels
            return (widthPx / Math.sqrt(screenSize * screenSize * widthPx * widthPx /
                    (widthPx * widthPx + heightPx * heightPx))).toFloat()
        }
    }
}