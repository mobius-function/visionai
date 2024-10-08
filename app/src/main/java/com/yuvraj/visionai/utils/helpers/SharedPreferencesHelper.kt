package com.yuvraj.visionai.utils.helpers

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import com.yuvraj.visionai.utils.Constants
import com.yuvraj.visionai.utils.Constants.ALL_IN_ONE_EYE_TEST
import com.yuvraj.visionai.utils.PowerAlgorithm.Companion.getFrontCameraFocalLength

object SharedPreferencesHelper {
    fun Activity.getAllInOneEyeTestMode(): Boolean {
        // Set all_in_one_test as false
        val sharedPreferences = getSharedPreferences(
            ALL_IN_ONE_EYE_TEST,
            AppCompatActivity.MODE_PRIVATE
        )

        return sharedPreferences.getBoolean(ALL_IN_ONE_EYE_TEST, false)
    }

    fun Activity.setAllInOneEyeTestMode(value : Boolean) {
        val sharedPreferences = getSharedPreferences(
            ALL_IN_ONE_EYE_TEST,
            AppCompatActivity.MODE_PRIVATE
        )

        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putBoolean(ALL_IN_ONE_EYE_TEST, value)
        sharedPreferencesEditor.apply()
    }

    fun Activity.setFocalLength(focalLength: Double) {
        val sharedPreferences = getSharedPreferences(
            "TESTING_DEBUG_VALUES",
            AppCompatActivity.MODE_PRIVATE
        )

        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putFloat("FOCAL_LENGTH", focalLength.toFloat())
        sharedPreferencesEditor.apply()
    }

    fun Activity.getFocalLength(): Double {
        val sharedPreferences = getSharedPreferences(
            "TESTING_DEBUG_VALUES",
            AppCompatActivity.MODE_PRIVATE
        )

        return sharedPreferences.getFloat("FOCAL_LENGTH", getFrontCameraFocalLength()).toDouble()
    }

    fun Activity.initiateAllInOneEyeTestMode() {
        setAllInOneEyeTestMode(true)

        val sharedPreferences = getSharedPreferences(
            Constants.EYE_TEST_RESULTS,
            AppCompatActivity.MODE_PRIVATE
        )

        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putLong(Constants.TOTAL_TIME_SPENT_TESTING, 0)
        sharedPreferencesEditor.putInt(Constants.LEFT_EYE_PARTIAL_BLINK_COUNTER, 0)
        sharedPreferencesEditor.putInt(Constants.RIGHT_EYE_PARTIAL_BLINK_COUNTER, 0)
        sharedPreferencesEditor.apply()
    }

    fun Activity.updateAllInOneEyeTestModeAfterTest(
        totalTimeSpent: Long,
        leftEyePartialBlinkCounter: Int,
        rightEyePartialBlinkCounter: Int
    ) {

        val sharedPreferences = getSharedPreferences(
            Constants.EYE_TEST_RESULTS,
            AppCompatActivity.MODE_PRIVATE
        )

        val totalTimeSpentBefore = sharedPreferences
            .getLong(Constants.TOTAL_TIME_SPENT_TESTING, 0)
        val leftEyePartialBlinkCounterBefore = sharedPreferences
            .getInt(Constants.LEFT_EYE_PARTIAL_BLINK_COUNTER, 0)
        val rightEyePartialBlinkCounterBefore = sharedPreferences
            .getInt(Constants.RIGHT_EYE_PARTIAL_BLINK_COUNTER, 0)

        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putLong(
            Constants.TOTAL_TIME_SPENT_TESTING,
            totalTimeSpent + totalTimeSpentBefore)

        sharedPreferencesEditor.putInt(
            Constants.LEFT_EYE_PARTIAL_BLINK_COUNTER,
            leftEyePartialBlinkCounter + leftEyePartialBlinkCounterBefore)

        sharedPreferencesEditor.putInt(
            Constants.RIGHT_EYE_PARTIAL_BLINK_COUNTER,
            rightEyePartialBlinkCounter + rightEyePartialBlinkCounterBefore)

        sharedPreferencesEditor.apply()
    }

    fun Activity.setPastMyopiaResults(leftEye: Double, rightEye: Double) {
        val sharedPreferences = getSharedPreferences(
            Constants.EYE_TEST_RESULTS,
            AppCompatActivity.MODE_PRIVATE
        )

        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putFloat(Constants.LATEST_LEFT_EYE_MYOPIA, leftEye.toFloat())
        sharedPreferencesEditor.putFloat(Constants.LATEST_RIGHT_EYE_MYOPIA, rightEye.toFloat())
        sharedPreferencesEditor.apply()
    }

    fun Activity.getPastMyopiaResults(): Pair<Double, Double> {
        val sharedPreferences = getSharedPreferences(
            Constants.EYE_TEST_RESULTS,
            AppCompatActivity.MODE_PRIVATE
        )

        val leftEye = sharedPreferences.getFloat(Constants.LATEST_LEFT_EYE_MYOPIA, 0f).toDouble()
        val rightEye = sharedPreferences.getFloat(Constants.LATEST_RIGHT_EYE_MYOPIA, 0f).toDouble()

        return Pair(leftEye, rightEye)
    }

    fun Activity.setPastHyperopiaResults(leftEye: Double, rightEye: Double) {
        val sharedPreferences = getSharedPreferences(
            Constants.EYE_TEST_RESULTS,
            AppCompatActivity.MODE_PRIVATE
        )

        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putFloat(Constants.LATEST_LEFT_EYE_HYPEROPIA, leftEye.toFloat())
        sharedPreferencesEditor.putFloat(Constants.LATEST_RIGHT_EYE_HYPEROPIA, rightEye.toFloat())
        sharedPreferencesEditor.apply()
    }

    fun Activity.getPastHyperopiaResults(): Pair<Double, Double> {
        val sharedPreferences = getSharedPreferences(
            Constants.EYE_TEST_RESULTS,
            AppCompatActivity.MODE_PRIVATE
        )

        val leftEye = sharedPreferences.getFloat(Constants.LATEST_LEFT_EYE_HYPEROPIA, 0f).toDouble()
        val rightEye = sharedPreferences.getFloat(Constants.LATEST_RIGHT_EYE_HYPEROPIA, 0f).toDouble()

        return Pair(leftEye, rightEye)
    }

    fun Activity.setPastAstigmatismResults(astigmatism: Double) {
        val sharedPreferences = getSharedPreferences(
            Constants.EYE_TEST_RESULTS,
            AppCompatActivity.MODE_PRIVATE
        )

        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putFloat(Constants.LATEST_ASTIGMATISM, astigmatism.toFloat())
        sharedPreferencesEditor.apply()
    }

    fun Activity.getPastAstigmatismResults(): Double {
        val sharedPreferences = getSharedPreferences(
            Constants.EYE_TEST_RESULTS,
            AppCompatActivity.MODE_PRIVATE
        )

        return sharedPreferences.getFloat(Constants.LATEST_ASTIGMATISM, 0f).toDouble()
    }

    fun Activity.setPastDryEyeResults(leftEye: Double, rightEye: Double) {
        val sharedPreferences = getSharedPreferences(
            Constants.EYE_TEST_RESULTS,
            AppCompatActivity.MODE_PRIVATE
        )

        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putFloat(Constants.LATEST_DRY_LEFT_EYE, leftEye.toFloat())
        sharedPreferencesEditor.putFloat(Constants.LATEST_DRY_RIGHT_EYE, rightEye.toFloat())
        sharedPreferencesEditor.apply()
    }

    fun Activity.getPastDryEyeResults(): Pair<Double, Double> {
        val sharedPreferences = getSharedPreferences(
            Constants.EYE_TEST_RESULTS,
            AppCompatActivity.MODE_PRIVATE
        )

        val leftEye = sharedPreferences.getFloat(Constants.LATEST_DRY_LEFT_EYE, 0f).toDouble()
        val rightEye = sharedPreferences.getFloat(Constants.LATEST_DRY_RIGHT_EYE, 0f).toDouble()

        return Pair(leftEye, rightEye)
    }
}