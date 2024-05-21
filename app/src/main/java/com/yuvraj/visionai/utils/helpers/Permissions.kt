package com.yuvraj.visionai.utils.helpers

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.yuvraj.visionai.utils.Constants

object Permissions {
    fun Activity.allPermissionsGranted(): Boolean {
        for (permission in Constants.REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
}