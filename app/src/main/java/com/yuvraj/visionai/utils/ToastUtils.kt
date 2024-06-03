package com.yuvraj.visionai.utils

import android.content.Context
import android.widget.Toast

class ToastUtils {
    companion object {
        fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
            Toast.makeText(context, message, duration).show()
        }
    }
}