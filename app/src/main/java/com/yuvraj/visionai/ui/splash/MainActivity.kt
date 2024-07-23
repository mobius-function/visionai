package com.yuvraj.visionai.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.UiSplashScreenBinding
import com.yuvraj.visionai.ui.onBoarding.MainActivity
import com.yuvraj.visionai.utils.ScreenUtils.hideSystemUI

class MainActivity : AppCompatActivity() {

    private var binding: UiSplashScreenBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        setContentView(binding?.root)

        hideSystemUI()

        // pause for 8 seconds
//        Thread.sleep(8000)

//        val intent = Intent(this, MainActivity::class.java)
//        startActivity(intent)

        binding?.apply {
            btnStart.visibility = View.VISIBLE
            btnStart.setOnClickListener {
                val intent = Intent(this@MainActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun initViews() {
        binding = UiSplashScreenBinding.inflate(layoutInflater,null,false)
    }
}