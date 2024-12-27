package com.yuvraj.visionai.utils

import android.app.Activity
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.Log
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.getFocalLength
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.getPastAstigmatismResults
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.getPastDryEyeResults
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.getPastHyperopiaResults
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.getPastMyopiaResults
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.absoluteValue

class PowerAlgorithm {

    companion object {
        fun generateEyeTestId(inputDate: String): String {
            // Input format matching the given date stamp
            val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'XXX yyyy", Locale.ENGLISH)
            // Desired output format for sorting
            val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)

            return try {
                // Parse the input date
                val date = inputFormat.parse(inputDate)
                // Format the parsed date into the desired output
                outputFormat.format(date)
            } catch (e: Exception) {
                // Handle parsing errors
                "2050-12-31 00:00:00 (Invalid date format)"
            }
        }

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

            val power = cumulativePowerLowerBound + ((cumulativePowerUpperBound - cumulativePowerLowerBound) * ((deno - denoLowerBound) / (denoUpperBound - denoLowerBound)))
            return if(power < 0.0f) {
                0.0f
            } else {
                power
            }
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

        fun Activity.calculateEyeHealthScore(): Float {
            val (plusPowerLeftEye, plusPowerRightEye) = getPastHyperopiaResults()
            val (minusPowerLeftEye, minusPowerRightEye) = getPastMyopiaResults()
            val (dryLeftEye, dryRightEye) = getPastDryEyeResults()
            val astigmatism = getPastAstigmatismResults()

            val sharedPreferences = getSharedPreferences(
                Constants.USER_DETAILS,
                Context.MODE_PRIVATE
            )

            val age = sharedPreferences.getInt(Constants.USER_AGE, 0)

            Log.d("Debug PowerAlgorithm", "Plus Power Left Eye: $plusPowerLeftEye" +
                    " Plus Power Right Eye: $plusPowerRightEye" +
                    " Minus Power Left Eye: $minusPowerLeftEye" +
                    " Minus Power Right Eye: $minusPowerRightEye" +
                    " Dry Left Eye: $dryLeftEye" +
                    " Dry Right Eye: $dryRightEye" +
                    " Astigmatism: $astigmatism" +
                    " Age: $age"
            )

            // Helper function to calculate eye-specific score
            fun calculateEyeScore(dryEye: Boolean, plusPower: Float, minusPower: Float): Float {
                var score = 100.0f

                // Deduct for dry eye
                if (dryEye) {
                    score -= 10.0f
                }

                // Deduct for Plus Power (farsightedness)
                score -= (plusPower.absoluteValue * 5.0f)

                // Deduct for Minus Power (nearsightedness)
                score -= (minusPower.absoluteValue * 5.0f)

                return score
            }

            // Calculate scores for left and right eyes
            val leftEyeScore = calculateEyeScore(dryLeftEye, plusPowerLeftEye, minusPowerLeftEye)
            val rightEyeScore = calculateEyeScore(dryRightEye, plusPowerRightEye, minusPowerRightEye)

            // Deduct for age equally from both eyes
            val ageDeduction = if (age > 20) (age - 20) * 0.5f else 0.0f
            val adjustedLeftEyeScore = (leftEyeScore - ageDeduction).coerceIn(0.0f, 100.0f)
            val adjustedRightEyeScore = (rightEyeScore - ageDeduction).coerceIn(0.0f, 100.0f)

            // Deduct for astigmatism equally from both eyes
            val finalLeftEyeScore = if (astigmatism) adjustedLeftEyeScore - 7.5f else adjustedLeftEyeScore
            val finalRightEyeScore = if (astigmatism) adjustedRightEyeScore - 7.5f else adjustedRightEyeScore

            // Calculate and return the average score
            return ((finalLeftEyeScore + finalRightEyeScore) / 2.0f).coerceIn(0.0f, 100.0f)
        }

    }
}