package com.yuvraj.visionai.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.UiSplashScreenBinding
import com.yuvraj.visionai.ui.onBoarding.MainActivity

class MainActivity : AppCompatActivity() {

    private var binding: UiSplashScreenBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initviews()
        setContentView(binding?.root)

        binding?.let {
            Glide.with(this)
                .load(R.drawable.anim_app_logo)
                .into(it.ivAnimLogo)
        }

        // pause for 8 seconds
//        Thread.sleep(8000)
//
//        val intent = Intent(this, MainActivity::class.java)
//        startActivity(intent)
//
//        finish()
    }

    fun initviews() {
        binding = UiSplashScreenBinding.inflate(layoutInflater,null,false)
    }

    fun showAnimation() {


//        finish()
    }
}