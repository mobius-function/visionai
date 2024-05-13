package com.yuvraj.visionai.utils

object Constants {
    // App's required permissions
    val REQUIRED_PERMISSIONS = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    // Boolean States
    const val USER_ONBOARDING_COMPLETED = "USER_ONBOARDING_COMPLETED"

    const val IS_ALL_PERMISSIONS_GRANTED = "IS_ALL_PERMISSIONS_GRANTED"

    const val FIRST_USE_AFTER_LOGIN = "FIRST_USE_AFTER_LOGIN"

    const val ALL_IN_ONE_EYE_TEST = "ALL_IN_ONE_EYE_TEST"

    // Test Results
    const val ALL_IN_ONE_EYE_TEST_RESULTS = "ALL_IN_ONE_EYE_TEST_RESULTS"
    const val MYOPIA_RESULTS = "MYOPIA_RESULTS"
    const val HYPEROPIA_RESULTS = "HYPEROPIA_RESULTS"
    const val ASTIGMATISM_RESULTS = "ASTIGMATISM_RESULTS"
    const val DRY_EYE_RESULTS = "DRY_EYE_RESULTS"

    // User Details
    const val USER_DETAILS = "USER_DETAILS"

    const val USER_NAME = "USER_NAME"
    const val USER_EMAIL = "USER_EMAIL"
    const val USER_PHONE = "USER_PHONE"                                     // Long
    const val USER_AGE = "USER_AGE"                                        // Int

    // Eye Tests IDs
    const val MYOPIA = "MYOPIA"
    const val HYPEROPIA = "HYPEROPIA"
    const val ASTIGMATISM = "ASTIGMATISM"
    const val DRY_EYE = "DRY_EYE"

    const val ML_MODEL = "ML_MODEL"
    const val LOGOUT = "LOGOUT"

    // Notification Settings Preferences
    const val NOTIFICATION_PREFERENCES = "NOTIFICATION_PREFERENCES"

    const val EYE_TEST_REMINDER = "EYE_TEST_REMINDER"                    // Boolean
    const val EYE_TEST_REMINDER_TIME = "EYE_TEST_REMINDER_TIME"         // Long

    const val REGULAR_REMINDER = "REGULAR_REMINDER"                     // Boolean
    const val REGULAR_REMINDER_TIME = "REGULAR_REMINDER_TIME"           // Long


}