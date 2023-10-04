package com.yuvraj.visionai.utils.helpers

import android.util.Patterns

object Validations {


    fun validateFirstName(data: String): Boolean {
        if (data.isEmpty()) {
            return false
        }
        return true
    }

    fun validateEmail(data: String): Boolean {
        if(Patterns.EMAIL_ADDRESS.matcher(data).matches()) {
            return true
        }
        return false
    }

    fun validatePassword(data: String): Boolean {
        if(data.length < 8) {
            return false
        }
        return true
    }

    fun validateConfirmPassword(data1: String, data2: String): Boolean {
        if(data1 == data2) {
            return true
        }
        return false
    }

    fun validatePhoneNumbers(phone1: String, phone2: String): Boolean {
        if(phone1 == phone2) {
            return false
        }
        return true
    }
}