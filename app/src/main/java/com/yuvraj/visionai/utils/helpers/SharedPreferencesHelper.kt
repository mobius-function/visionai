package com.yuvraj.visionai.utils.helpers

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import com.yuvraj.visionai.model.EyePower
import com.yuvraj.visionai.utils.Constants
import com.yuvraj.visionai.utils.Constants.ALL_IN_ONE_EYE_TEST
import com.yuvraj.visionai.utils.PowerAlgorithm.Companion.getFrontCameraFocalLength

object SharedPreferencesHelper {
    fun Activity.setLastEyeTestDate(date: String) {
        val sharedPreferences = getSharedPreferences(
            Constants.USER_DETAILS,
            AppCompatActivity.MODE_PRIVATE
        )

        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putString(Constants.LAST_EYE_TEST_DATE, date)
        sharedPreferencesEditor.apply()
    }


    fun Activity.getLastEyeTestDate(): String {
        val sharedPreferences = getSharedPreferences(
            Constants.USER_DETAILS,
            AppCompatActivity.MODE_PRIVATE
        )

        return sharedPreferences.getString(Constants.LAST_EYE_TEST_DATE, "")!!
    }


    fun Activity.isDebugMode(): Boolean {
        val sharedPreferences = getSharedPreferences(
            Constants.USER_DETAILS,
            AppCompatActivity.MODE_PRIVATE
        )

        return sharedPreferences.getBoolean(Constants.DEBUG_MODE_OPTION, false)
    }

    fun Activity.setDebugMode(isDebugMode: Boolean) {
        val sharedPreferences = getSharedPreferences(
            Constants.USER_DETAILS,
            AppCompatActivity.MODE_PRIVATE
        )

        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putBoolean(Constants.DEBUG_MODE_OPTION, isDebugMode)
        sharedPreferencesEditor.apply()
    }

    fun Activity.openedAppFirstTime(hasOpened: Boolean) {
        val sharedPreferences = getSharedPreferences(
            Constants.USER_DETAILS,
            AppCompatActivity.MODE_PRIVATE
        )

        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putBoolean("OPENED_APP_FIRST_TIME", hasOpened)
        sharedPreferencesEditor.apply()
    }

    fun Activity.hasOpenedAppFirstTime(): Boolean {
        val sharedPreferences = getSharedPreferences(
            Constants.USER_DETAILS,
            AppCompatActivity.MODE_PRIVATE
        )

        return sharedPreferences.getBoolean("OPENED_APP_FIRST_TIME", true)
    }

    fun Activity.openedLandingPage(hasOpened: Boolean) {
        val sharedPreferences = getSharedPreferences(
            Constants.USER_DETAILS,
            AppCompatActivity.MODE_PRIVATE
        )

        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putBoolean("OPENED_LANDING_PAGE", hasOpened)
        sharedPreferencesEditor.apply()
    }

    fun Activity.hasOpenedLandingPage(): Boolean {
        val sharedPreferences = getSharedPreferences(
            Constants.USER_DETAILS,
            AppCompatActivity.MODE_PRIVATE
        )

        return sharedPreferences.getBoolean("OPENED_LANDING_PAGE", false)
    }

    fun Activity.openedNotificationPage(hasOpened: Boolean) {
        val sharedPreferences = getSharedPreferences(
            Constants.USER_DETAILS,
            AppCompatActivity.MODE_PRIVATE
        )

        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putBoolean("OPENED_NOTIFICATION_PAGE", hasOpened)
        sharedPreferencesEditor.apply()
    }

    fun Activity.hasOpenedNotificationPage(): Boolean {
        val sharedPreferences = getSharedPreferences(
            Constants.USER_DETAILS,
            AppCompatActivity.MODE_PRIVATE
        )

        return sharedPreferences.getBoolean("OPENED_NOTIFICATION_PAGE", false)
    }

    fun Activity.openedProfilePage(hasOpened: Boolean) {
        val sharedPreferences = getSharedPreferences(
            Constants.USER_DETAILS,
            AppCompatActivity.MODE_PRIVATE
        )

        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putBoolean("OPENED_PROFILE_PAGE", hasOpened)
        sharedPreferencesEditor.apply()
    }

    fun Activity.hasOpenedProfilePage(): Boolean {
        val sharedPreferences = getSharedPreferences(
            Constants.USER_DETAILS,
            AppCompatActivity.MODE_PRIVATE
        )

        return sharedPreferences.getBoolean("OPENED_PROFILE_PAGE", false)
    }

    fun Activity.openedReportPage(hasOpened: Boolean) {
        val sharedPreferences = getSharedPreferences(
            Constants.USER_DETAILS,
            AppCompatActivity.MODE_PRIVATE
        )

        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putBoolean("OPENED_REPORT_PAGE", hasOpened)
        sharedPreferencesEditor.apply()
    }

    fun Activity.hasOpenedReportPage(): Boolean {
        val sharedPreferences = getSharedPreferences(
            Constants.USER_DETAILS,
            AppCompatActivity.MODE_PRIVATE
        )

        return sharedPreferences.getBoolean("OPENED_REPORT_PAGE", false)
    }

    fun Activity.openedChatBotPage(hasOpened: Boolean) {
        val sharedPreferences = getSharedPreferences(
            Constants.USER_DETAILS,
            AppCompatActivity.MODE_PRIVATE
        )

        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putBoolean("OPENED_CHAT_BOT_PAGE", hasOpened)
        sharedPreferencesEditor.apply()
    }

    fun Activity.hasOpenedChatBotPage(): Boolean {
        val sharedPreferences = getSharedPreferences(
            Constants.USER_DETAILS,
            AppCompatActivity.MODE_PRIVATE
        )

        return sharedPreferences.getBoolean("OPENED_CHAT_BOT_PAGE", false)
    }

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

    fun Activity.setPastMyopiaResults(leftEye: Float, rightEye: Float) {
        val sharedPreferences = getSharedPreferences(
            Constants.EYE_TEST_RESULTS,
            AppCompatActivity.MODE_PRIVATE
        )

        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putFloat(Constants.LATEST_LEFT_EYE_MYOPIA, leftEye.toFloat())
        sharedPreferencesEditor.putFloat(Constants.LATEST_RIGHT_EYE_MYOPIA, rightEye.toFloat())
        sharedPreferencesEditor.apply()
    }

    fun Activity.getPastMyopiaResults(): EyePower {
        val sharedPreferences = getSharedPreferences(
            Constants.EYE_TEST_RESULTS,
            AppCompatActivity.MODE_PRIVATE
        )

        val leftEye = sharedPreferences.getFloat(Constants.LATEST_LEFT_EYE_MYOPIA, 0f)
        val rightEye = sharedPreferences.getFloat(Constants.LATEST_RIGHT_EYE_MYOPIA, 0f)

        return EyePower(leftEye, rightEye)
    }

    fun Activity.setPastHyperopiaResults(leftEye: Float, rightEye: Float) {
        val sharedPreferences = getSharedPreferences(
            Constants.EYE_TEST_RESULTS,
            AppCompatActivity.MODE_PRIVATE
        )

        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putFloat(Constants.LATEST_LEFT_EYE_HYPEROPIA, leftEye.toFloat())
        sharedPreferencesEditor.putFloat(Constants.LATEST_RIGHT_EYE_HYPEROPIA, rightEye.toFloat())
        sharedPreferencesEditor.apply()
    }

    fun Activity.getPastHyperopiaResults(): EyePower {
        val sharedPreferences = getSharedPreferences(
            Constants.EYE_TEST_RESULTS,
            AppCompatActivity.MODE_PRIVATE
        )

        val leftEye = sharedPreferences.getFloat(Constants.LATEST_LEFT_EYE_HYPEROPIA, 0f)
        val rightEye = sharedPreferences.getFloat(Constants.LATEST_RIGHT_EYE_HYPEROPIA, 0f)

        return EyePower(leftEye, rightEye)
    }

    fun Activity.setPastAstigmatismResults(astigmatism: Boolean) {
        val sharedPreferences = getSharedPreferences(
            Constants.EYE_TEST_RESULTS,
            AppCompatActivity.MODE_PRIVATE
        )

        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putBoolean(Constants.LATEST_ASTIGMATISM, astigmatism)
        sharedPreferencesEditor.apply()
    }

    fun Activity.getPastAstigmatismResults(): Boolean {
        val sharedPreferences = getSharedPreferences(
            Constants.EYE_TEST_RESULTS,
            AppCompatActivity.MODE_PRIVATE
        )

        return sharedPreferences.getBoolean(Constants.LATEST_ASTIGMATISM, false)
    }

    fun Activity.setPastDryEyeResults(leftEye: Boolean, rightEye: Boolean) {
        val sharedPreferences = getSharedPreferences(
            Constants.EYE_TEST_RESULTS,
            AppCompatActivity.MODE_PRIVATE
        )

        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putBoolean(Constants.LATEST_DRY_LEFT_EYE, leftEye)
        sharedPreferencesEditor.putBoolean(Constants.LATEST_DRY_RIGHT_EYE, rightEye)
        sharedPreferencesEditor.apply()
    }

    fun Activity.getPastDryEyeResults(): Pair<Boolean, Boolean> {
        val sharedPreferences = getSharedPreferences(
            Constants.EYE_TEST_RESULTS,
            AppCompatActivity.MODE_PRIVATE
        )

        val leftEye = sharedPreferences.getBoolean(Constants.LATEST_DRY_LEFT_EYE, false)
        val rightEye = sharedPreferences.getBoolean(Constants.LATEST_DRY_RIGHT_EYE, false)

        return Pair(leftEye, rightEye)
    }
}