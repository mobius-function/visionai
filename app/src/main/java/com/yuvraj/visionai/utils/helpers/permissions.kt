package com.yuvraj.visionai.utils.helpers

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.yuvraj.visionai.ui.home.MainActivity
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