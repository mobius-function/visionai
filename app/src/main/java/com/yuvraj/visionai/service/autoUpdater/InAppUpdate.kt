package com.yuvraj.visionai.service.autoUpdater

import android.app.Activity
import android.content.IntentSender.SendIntentException
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.yuvraj.visionai.utils.ToastUtils.Companion.showToast


class InAppUpdate(private val parentActivity: Activity) {
    private val appUpdateManager: AppUpdateManager?
    private val appUpdateType = AppUpdateType.FLEXIBLE

    private var stateUpdatedListener =
        InstallStateUpdatedListener { installState: InstallState ->
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
//                popupSnackBarForCompleteUpdate()
                //show toast
                Toast.makeText(parentActivity.applicationContext, "An update has just been downloaded.", Toast.LENGTH_SHORT).show()
            }
        }

    init {
        appUpdateManager = AppUpdateManagerFactory.create(parentActivity)
    }

    fun checkForAppUpdate() {
        appUpdateManager!!.appUpdateInfo.addOnSuccessListener { info: AppUpdateInfo ->
            val isUpdateAvailable =
                info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = info.isUpdateTypeAllowed(appUpdateType)
            if (isUpdateAvailable && isUpdateAllowed) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        info,
                        appUpdateType,
                        parentActivity,
                        MY_REQUEST_CODE
                    )
                } catch (e: SendIntentException) {
                    throw RuntimeException(e)
                }
            }
        }
        appUpdateManager.registerListener(stateUpdatedListener)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int) {
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode == RESULT_CANCELED) {
                showToast(parentActivity.applicationContext, "Update canceled by user")
                Toast.makeText(parentActivity.applicationContext, "Update canceled by user", Toast.LENGTH_SHORT).show()
            } else if (resultCode != AppCompatActivity.RESULT_OK) {
                checkForAppUpdate()
            }
        }
    }

//    private fun popupSnackBarForCompleteUpdate() {
//        Snackbar.make(
//            parentActivity.findViewById<View>(R.id.postsFrameLayout),
//            "An update has just been downloaded.",
//            Snackbar.LENGTH_INDEFINITE
//        ).setAction(
//            "RESTART"
//        ) { view: View? ->
//            appUpdateManager?.completeUpdate()
//        }.show()
//    }

    fun onResume() {
        appUpdateManager?.appUpdateInfo?.addOnSuccessListener { info: AppUpdateInfo ->
            if (info.installStatus() == InstallStatus.DOWNLOADED) {
//                popupSnackBarForCompleteUpdate()
                //show toast
                Toast.makeText(parentActivity.applicationContext, "An update has just been downloaded.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun onDestroy() {
        appUpdateManager?.unregisterListener(stateUpdatedListener)
    }

    companion object{
        const val RESULT_CANCELED = 0
        const val MY_REQUEST_CODE = 500
    }
}