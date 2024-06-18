package com.yuvraj.visionai.ui.onBoarding

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.UiOnboardingActivityMainBinding
import com.yuvraj.visionai.service.autoUpdater.InAppUpdate
import com.yuvraj.visionai.utils.ScreenUtils.hideSystemUI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {

    private var binding: UiOnboardingActivityMainBinding? = null
    private lateinit var navHostFragment: NavHostFragment

    //In-App Update manager
    private var inAppUpdate: InAppUpdate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initviews()
        setContentView(binding?.root)
        hideSystemUI()
    }

    fun initviews() {
        binding = UiOnboardingActivityMainBinding.inflate(layoutInflater,null,false)

        //In-App Update Initializer
        inAppUpdate = InAppUpdate(this@MainActivity)
        inAppUpdate!!.onStart()
        inAppUpdate!!.checkUpdate()

        navHostFragment = (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment)
    }

    override fun onResume() {
        super.onResume()
        inAppUpdate!!.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        inAppUpdate!!.onDestroy()
    }
}