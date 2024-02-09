package com.yuvraj.visionai.utils.helpers

import android.util.Patterns

object Validations {

    fun validateFirstName(data: String): Boolean {
        return !data.isEmpty()
    }

    fun validateEmail(data: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(data).matches()
    }

    fun validatePassword(data: String): Boolean {
        return data.length >= 8
    }

    fun validateConfirmPassword(data1: String, data2: String): Boolean {
        return data1 == data2
    }
}