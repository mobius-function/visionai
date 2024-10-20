package com.yuvraj.visionai.utils

import android.app.Activity
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.Log
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.getFocalLength

class PowerAlgorithm {

    companion object {
        fun generateInitialPowerText(): Double {
            return (0.145 * 6 * 3) / 8
        }

        fun Activity.calculateFocalLength(): Double {
            return getFocalLength()
        }

        fun Activity.getFrontCameraFocalLength(): Float {
            val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraIdList = cameraManager.cameraIdList
            for (id in cameraIdList) {
                val characteristics = cameraManager.getCameraCharacteristics(id)
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    val focalLengths = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)
                    if (focalLengths != null && focalLengths.isNotEmpty()) {
                        return focalLengths[0]*10 // or calculate an average if there are multiple
                    }
                }
            }
            Log.e("FocalLength", "Front camera focal length not found")
            return getDefaultFocalLength()
        }

        fun getDefaultFocalLength(): Float {
            /*
                Abhiram android 13 - 23.64
                Kunal android 13 - 24.71
                Aditya android 14 - 25.962
                Abhinav android 12 - 28.1747   38.1747
            */

            return if (Build.VERSION.SDK_INT <= 30) {
                32.0f
            } else if (Build.VERSION.SDK_INT == 31 || Build.VERSION.SDK_INT == 32) {
                25.0f
            } else if (Build.VERSION.SDK_INT >= 33) {
                17.0f
            } else {
                16.0f
            }
        }

        fun calculatePositivePower(lastIncorrect: Float, age: Int, baseDistance: Float) : Float {

            val distance = (baseDistance * lastIncorrect / 0.50905435)

            val ageFactor: Int
            if(age < 10){
                ageFactor = 70
            } else if(age < 20){
                ageFactor = 90
            } else if(age < 30){
                ageFactor = 120
            } else if(age < 40){
                ageFactor = 220
            } else if(age < 45){
                ageFactor = 280
            } else if(age < 50){
                ageFactor = 400
            }else if(age < 55) {
                ageFactor = 550
            } else if(age < 60){
                ageFactor = 1000
            } else if(age < 65){
                ageFactor = 1330
            } else{
                ageFactor = 4000
            }

            return (1000/(ageFactor-15) - 1000/(distance-15)).toFloat()
        }

        fun calculatePositivePowerWithDeno(deno: Float) : Float {
            var denoLowerBound = 20.0f
            var denoUpperBound = 25.0f

            var cumulativePowerLowerBound = 0.00f
            var cumulativePowerUpperBound = 0.25f

            if(deno <= 20) {
                denoLowerBound = 20.0f
                denoUpperBound = 25.0f
                cumulativePowerLowerBound = 0.00f
                cumulativePowerUpperBound = 0.25f
            } else if(deno <= 25) {
                denoLowerBound = 25.0f
                denoUpperBound = 30.0f
                cumulativePowerLowerBound = 0.25f
                cumulativePowerUpperBound = 0.50f
            } else if(deno <= 30) {
                denoLowerBound = 30.0f
                denoUpperBound = 40.0f
                cumulativePowerLowerBound = 0.50f
                cumulativePowerUpperBound = 0.75f
            } else if(deno <= 40) {
                denoLowerBound = 40.0f
                denoUpperBound = 50.0f
                cumulativePowerLowerBound = 0.75f
                cumulativePowerUpperBound = 1.00f
            } else if(deno <= 50) {
                denoLowerBound = 50.0f
                denoUpperBound = 70.0f
                cumulativePowerLowerBound = 1.00f
                cumulativePowerUpperBound = 1.50f
            } else if(deno <= 70) {
                denoLowerBound = 70.0f
                denoUpperBound = 100.0f
                cumulativePowerLowerBound = 1.50f
                cumulativePowerUpperBound = 2.00f
            } else if(deno <= 100) {
                denoLowerBound = 100.0f
                denoUpperBound = 200.0f
                cumulativePowerLowerBound = 2.00f
                cumulativePowerUpperBound = 2.50f
            } else if(deno <= 200) {
                denoLowerBound = 200.0f
                denoUpperBound = 250.0f
                cumulativePowerLowerBound = 2.50f
                cumulativePowerUpperBound = 3.00f
            } else if(deno <= 250) {
                denoLowerBound = 250.0f
                denoUpperBound = 300.0f
                cumulativePowerLowerBound = 3.00f
                cumulativePowerUpperBound = 3.50f
            } else if(deno <= 300) {
                denoLowerBound = 300.0f
                denoUpperBound = 400.0f
                cumulativePowerLowerBound = 3.50f
                cumulativePowerUpperBound = 4.00f
            } else if(deno <= 400){
                denoLowerBound = 400.0f
                denoUpperBound = 500.0f
                cumulativePowerLowerBound = 4.00f
                cumulativePowerUpperBound = 5.00f
            } else {
                denoLowerBound = 500.0f
                denoUpperBound = 1000.0f
                cumulativePowerLowerBound = 5.00f
                cumulativePowerUpperBound = 7.00f
            }

            return cumulativePowerLowerBound + ((cumulativePowerUpperBound - cumulativePowerLowerBound) * ((deno - denoLowerBound) / (denoUpperBound - denoLowerBound)))
        }

        fun calculateNegativePower(deno: Double) : Float {

            var cumulativePowerLowerBound : Float = 0.0f
            var cumulativePowerUpperBound : Float = 0.0f

            var denoLowerBound : Float = 0.0f
            var denoUpperBound : Float = 0.0f

            if(deno < 20) {
                cumulativePowerLowerBound = 0.00f
                cumulativePowerUpperBound = 0.00f
                denoLowerBound = 0.0f
                denoUpperBound = 20.0f
            } else if(deno < 25) {
                cumulativePowerLowerBound = 0.00f
                cumulativePowerUpperBound = -0.25f
                denoLowerBound = 20.0f
                denoUpperBound = 25.0f
            } else if(deno < 30) {
                cumulativePowerLowerBound = -0.25f
                cumulativePowerUpperBound = -0.50f
                denoLowerBound = 25.0f
                denoUpperBound = 30.0f
            } else if(deno < 40) {
                cumulativePowerLowerBound = -0.50f
                cumulativePowerUpperBound = -0.75f
                denoLowerBound = 30.0f
                denoUpperBound = 40.0f
            } else if(deno < 50) {
                cumulativePowerLowerBound = -0.75f
                cumulativePowerUpperBound = -1.00f
                denoLowerBound = 40.0f
                denoUpperBound = 50.0f
            } else if(deno < 70) {
                cumulativePowerLowerBound = -1.00f
                cumulativePowerUpperBound = -1.25f
                denoLowerBound = 50.0f
                denoUpperBound = 70.0f
            } else if(deno < 100) {
                cumulativePowerLowerBound = -1.25f
                cumulativePowerUpperBound = -1.50f
                denoLowerBound = 70.0f
                denoUpperBound = 100.0f
            } else if(deno < 150) {
                cumulativePowerLowerBound = -1.50f
                cumulativePowerUpperBound = -2.00f
                denoLowerBound = 100.0f
                denoUpperBound = 150.0f
            } else if(deno < 200) {
                cumulativePowerLowerBound = -2.00f
                cumulativePowerUpperBound = -2.50f
                denoLowerBound = 150.0f
                denoUpperBound = 200.0f
            } else if(deno < 250) {
                cumulativePowerLowerBound = -2.50f
                cumulativePowerUpperBound = -3.00f
                denoLowerBound = 200.0f
                denoUpperBound = 250.0f
            } else if(deno < 300) {
                cumulativePowerLowerBound = -3.00f
                cumulativePowerUpperBound = -3.50f
                denoLowerBound = 250.0f
                denoUpperBound = 300.0f
            } else if(deno < 400) {
                cumulativePowerLowerBound = -3.50f
                cumulativePowerUpperBound = -4.00f
                denoLowerBound = 300.0f
                denoUpperBound = 400.0f
            } else {
                cumulativePowerLowerBound = -5.00f
                cumulativePowerUpperBound = -5.00f
                denoLowerBound = 400.0f
                denoUpperBound = 1000.0f
            }


            val cumulativePower = cumulativePowerLowerBound +
                                    ((cumulativePowerUpperBound - cumulativePowerLowerBound)
                                            * ((deno - denoLowerBound) /
                                            (denoUpperBound - denoLowerBound)))



            return cumulativePower.toFloat()
        }
    }

    // On Boarding - Done
    // Navigation in Home
    // profile page
    // firebase - Done
    // logo - done
    // tracking in new page - done
    // changing it to text from speech - done
    // Newton Raphson
    // Blind app
    // Econ Job Rumours
    // QubeRT

    // WSO
}