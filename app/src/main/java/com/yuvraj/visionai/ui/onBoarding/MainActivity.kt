package com.yuvraj.visionai.ui.onBoarding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.UiOnboardingActivityMainBinding

class MainActivity: AppCompatActivity() {

    private var binding: UiOnboardingActivityMainBinding? = null
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initviews()
        setContentView(binding?.root)
    }

    fun initviews() {
        binding = UiOnboardingActivityMainBinding.inflate(layoutInflater,null,false)
        navHostFragment = (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment)
    }
}