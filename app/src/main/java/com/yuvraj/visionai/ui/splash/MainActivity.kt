package com.yuvraj.visionai.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.UiSplashScreenBinding

class MainActivity : AppCompatActivity() {

    private var binding: UiSplashScreenBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initviews()
        setContentView(binding?.root)
    }

    fun initviews() {
        binding = UiSplashScreenBinding.inflate(layoutInflater,null,false)


        // pause for 8 seconds
        Thread.sleep(8000)

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}