package com.yuvraj.visionai.utils

import android.Manifest
import android.os.Build

object Constants {
    // App's required permissions
    const val REQUEST_CODE_PERMISSION_FOR_CAMERA = 100
    const val REQUEST_CODE_PERMISSION_FOR_NOTIFICATIONS = 101

    val REQUIRED_PERMISSIONS = mutableListOf (
            Manifest.permission.CAMERA,
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                add(Manifest.permission.SCHEDULE_EXACT_ALARM)
            }

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }.toTypedArray()

    val REQUIRED_PERMISSIONS_FOR_CAMERA = arrayOf(
        android.Manifest.permission.CAMERA
    )

    // Test Results
    const val EYE_TEST_RESULTS = "EYE_TEST_RESULTS"

    const val LEFT_EYE_PARTIAL_BLINK_COUNTER = "LEFT_EYE_PARTIAL_BLINK_COUNTER"
    const val RIGHT_EYE_PARTIAL_BLINK_COUNTER = "RIGHT_EYE_PARTIAL_BLINK_COUNTER"
    const val TOTAL_TIME_SPENT_TESTING = "TOTAL_TIME_SPENT_TESTING"
    const val ALL_IN_ONE_EYE_TEST_RESULTS = "ALL_IN_ONE_EYE_TEST_RESULTS"
    const val MYOPIA_RESULTS_LEFT_EYE = "MYOPIA_RESULTS_LEFT_EYE"
    const val MYOPIA_RESULTS_RIGHT_EYE = "MYOPIA_RESULTS_RIGHT_EYE"
    const val HYPEROPIA_RESULTS_LEFT_EYE = "HYPEROPIA_RESULTS_LEFT_EYE"
    const val HYPEROPIA_RESULTS_RIGHT_EYE = "HYPEROPIA_RESULTS_RIGHT_EYE"
    const val ASTIGMATISM_RESULTS = "ASTIGMATISM_RESULTS"
    const val DRY_EYE_RESULTS = "DRY_EYE_RESULTS"


    // Test Configs
    val DRY_EYE_RANGE = 0.4..0.7


    // User Details
    const val USER_DETAILS = "USER_DETAILS"

    const val USER_ONBOARDING_COMPLETED = "USER_ONBOARDING_COMPLETED"
    const val IS_ALL_PERMISSIONS_GRANTED = "IS_ALL_PERMISSIONS_GRANTED"
    const val FIRST_USE_AFTER_LOGIN = "FIRST_USE_AFTER_LOGIN"
    const val ALL_IN_ONE_EYE_TEST = "ALL_IN_ONE_EYE_TEST"

    const val USER_NAME = "USER_NAME"
    const val USER_EMAIL = "USER_EMAIL"
    const val USER_PHONE = "USER_PHONE"                                     // Long
    const val USER_AGE = "USER_AGE"                                        // Int

    // past test results inside user details
    const val LATEST_LEFT_EYE_MYOPIA = "LATEST_LEFT_EYE_MYOPIA"
    const val LATEST_RIGHT_EYE_MYOPIA = "LATEST_RIGHT_EYE_MYOPIA"
    const val LATEST_LEFT_EYE_HYPEROPIA = "LATEST_LEFT_EYE_HYPEROPIA"
    const val LATEST_RIGHT_EYE_HYPEROPIA = "LATEST_RIGHT_EYE_HYPEROPIA"
    const val LATEST_ASTIGMATISM = "LATEST_ASTIGMATISM"
    const val LATEST_DRY_LEFT_EYE = "LATEST_DRY_LEFT_EYE"
    const val LATEST_DRY_RIGHT_EYE = "LATEST_DRY_RIGHT_EYE"


    // Eye Tests IDs
    const val MYOPIA = "MYOPIA"
    const val HYPEROPIA = "HYPEROPIA"
    const val ASTIGMATISM = "ASTIGMATISM"
    const val DRY_EYE = "DRY_EYE"

    const val ML_MODEL = "ML_MODEL"
    const val LOGOUT = "LOGOUT"

    // Eye Test Constants
    const val MAX_READINGS = 8
    const val MIN_DISPLAYED_TEXT_SIZE = 0.25f
    const val MAX_DISPLAYED_TEXT_SIZE = 30.0f


    // Notification Settings Preferences
    const val NOTIFICATION_PREFERENCES = "NOTIFICATION_PREFERENCES"

    const val EYE_TEST_REMINDER = "EYE_TEST_REMINDER"                    // Boolean
    const val EYE_TEST_REMINDER_TIME = "EYE_TEST_REMINDER_TIME"         // Long
    const val DEFAULT_EYE_TEST_REMINDER_TIME = 10                        // Int

    const val REGULAR_REMINDER = "REGULAR_REMINDER"                     // Boolean
    const val REGULAR_REMINDER_TIME = "REGULAR_REMINDER_TIME"           // Long
    const val DEFAULT_REGULAR_REMINDER_TIME = 2                         // Int

    // ChatBot API
    const val CHAT_BASE_URL="xxx"
    const val CHAT_AUTHORIZATION="Bearer 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"

}