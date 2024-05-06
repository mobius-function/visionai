package com.yuvraj.visionai.utils.helpers

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import com.yuvraj.visionai.utils.Constants.ALL_IN_ONE_EYE_TEST

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
}