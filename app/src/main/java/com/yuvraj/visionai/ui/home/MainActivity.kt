package com.yuvraj.visionai.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.UiHomeActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var binding: UiHomeActivityMainBinding? = null
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initviews()
        setContentView(binding?.root)
    }

    fun initviews() {
        binding = UiHomeActivityMainBinding.inflate(layoutInflater,null,false)
        navHostFragment = (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment)
    }
}