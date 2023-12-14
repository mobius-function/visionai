package com.yuvraj.visionai.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.UiHomeActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: UiHomeActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initviews()
        setContentView(binding.root)
    }

    fun initviews() {
        _binding = UiHomeActivityMainBinding.inflate(layoutInflater,null,false)

        navHostFragment = (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment)

//        val navController = navHostFragment.navController
//        binding.bottomNavigation.setupWithNavController(navController)

//        navHostFragment.findNavController().addOnDestinationChangedListener { _, destination, _ ->
//            when(destination.id) {
//                R.id.homeFragment, R.id.searchFragment, R.id.uploadFragment,R.id.notificationFragment , R.id.profileFragment ->
//                    binding!!.bottomNavigation.visibility = View.VISIBLE
//                else -> binding!!.bottomNavigation.visibility = View.GONE
//            }
//
//            when(destination.id) {
//                R.id.homeEyeTestingFragment, R.id.homeLandingFragment -> binding.appBarLayout.visibility = View.GONE
//                else -> binding.appBarLayout.visibility = View.VISIBLE
//            }
//        }
    }
}