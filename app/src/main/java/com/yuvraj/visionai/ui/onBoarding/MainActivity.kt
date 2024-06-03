package com.yuvraj.visionai.ui.onBoarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.UiOnboardingActivityMainBinding
import com.yuvraj.visionai.service.autoUpdater.InAppUpdate
import com.yuvraj.visionai.utils.ScreenUtils.hideSystemUI


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
        inAppUpdate!!.checkForAppUpdate()

        navHostFragment = (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment)
    }

    // Override methods for In-App Update
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        inAppUpdate!!.onActivityResult(requestCode, resultCode)
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