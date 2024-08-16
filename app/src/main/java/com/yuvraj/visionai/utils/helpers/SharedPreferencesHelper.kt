package com.yuvraj.visionai.utils.helpers

import android.app.Activity
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.yuvraj.visionai.utils.Constants
import com.yuvraj.visionai.utils.Constants.ALL_IN_ONE_EYE_TEST
import com.yuvraj.visionai.utils.PowerAlgorithm.Companion.getDefaultFocalLength

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

        return sharedPreferences.getFloat("FOCAL_LENGTH", getDefaultFocalLength()).toDouble()
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
}