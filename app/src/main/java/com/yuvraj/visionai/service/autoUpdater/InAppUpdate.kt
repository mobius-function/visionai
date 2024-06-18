package com.yuvraj.visionai.service.autoUpdater

import android.app.Activity
import android.content.IntentSender
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.common.IntentSenderForResultStarter
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability


class InAppUpdate(private val parentActivity: Activity) {
    private val appUpdateManager: AppUpdateManager?
    private val appUpdateType = AppUpdateType.FLEXIBLE

    init {
        appUpdateManager = AppUpdateManagerFactory.create(parentActivity)
    }

    fun checkUpdate() {
        val appUpdateInfoTask = appUpdateManager?.appUpdateInfo
        appUpdateInfoTask?.addOnSuccessListener { appUpdateInfo ->
            // This example applies an flexible update. To apply a immediate update
            // instead, pass in AppUpdateType.IMMEDIATE
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                // Request the update
                try {
                    appUpdateManager?.startUpdateFlowForResult(
                        // Pass the intent that is returned by 'getAppUpdateInfo()'.
                        appUpdateInfo,
                        // an activity result launcher registered via registerForActivityResult
                        updateResultStarter,
                        //pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
                        // flexible updates.
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build(),
                        // Include a request code to later monitor this update request.
                        UPDATE_REQUEST_CODE
                    )
                } catch (exception: IntentSender.SendIntentException) {
                    Toast.makeText(parentActivity, "Something wrong went wrong!", Toast.LENGTH_SHORT).show()
                }
            } else if(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){
                // Request the update
                try {
                    appUpdateManager?.startUpdateFlowForResult(
                        // Pass the intent that is returned by 'getAppUpdateInfo()'.
                        appUpdateInfo,
                        // an activity result launcher registered via registerForActivityResult
                        updateResultStarter,
                        //pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
                        // flexible updates.
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
                        // Include a request code to later monitor this update request.
                        UPDATE_REQUEST_CODE
                    )
                } catch (exception: IntentSender.SendIntentException) {
                    Toast.makeText(parentActivity, "Something wrong went wrong!", Toast.LENGTH_SHORT).show()
                }

                } else {
                Log.d("DebugInAppUpdate", "No Update available")
            }
        }
    }

    private val updateLauncher = AppCompatActivity().registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        // handle callback
        if (result.data == null) return@registerForActivityResult
        if (result.resultCode == UPDATE_REQUEST_CODE) {
            Toast.makeText(parentActivity, "Downloading stated", Toast.LENGTH_SHORT).show()

            if (result.resultCode != Activity.RESULT_OK) {
                Toast.makeText(parentActivity, "Downloading failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val updateResultStarter =
        IntentSenderForResultStarter { intent, _, fillInIntent, flagsMask, flagsValues, _, _ ->
            val request = IntentSenderRequest.Builder(intent)
                .setFillInIntent(fillInIntent)
                .setFlags(flagsValues, flagsMask)
                .build()
            // launch updateLauncher
            updateLauncher.launch(request)
        }

    private val listener = InstallStateUpdatedListener { state ->
        // (Optional) Provide a download progress bar.
        // TODO: Implement progress bar (Optional)
        if (state.installStatus() == InstallStatus.DOWNLOADING) {
            val bytesDownloaded = state.bytesDownloaded()
            val totalBytesToDownload = state.totalBytesToDownload()
            val progress = (bytesDownloaded * 100 / totalBytesToDownload).toInt()
            // Show update progress bar.
        }
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            Snackbar.make(
                parentActivity.findViewById(android.R.id.content),
                "New app is ready",
                Snackbar.LENGTH_INDEFINITE
            ).setAction("Restart") {
                appUpdateManager?.completeUpdate()
            }.show()
        }
        // Log state or install the update.
    }

    fun onStart() {
        appUpdateManager?.registerListener(listener)
    }

    fun onResume() {
        appUpdateManager?.completeUpdate()

        appUpdateManager?.appUpdateInfo?.addOnSuccessListener{ appUpdateInfo ->
            if (appUpdateInfo.updateAvailability()
                == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
            ) {
                // If an in-app update is already running, resume the update.
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    updateLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build())
            }
        }
    }

    fun onDestroy() {
        appUpdateManager?.unregisterListener(listener)
    }

    companion object{
        const val UPDATE_REQUEST_CODE = 303
    }
}