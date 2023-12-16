package com.yuvraj.visionai.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.toIcon
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.UiHomeActivityMainBinding
import com.yuvraj.visionai.firebase.Authentication.Companion.getSignedInUser

class MainActivity : AppCompatActivity() {

    private var _binding: UiHomeActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initviews()
        setContentView(binding.root)
        setupAppBar()
        setupNavigationController()
    }

    fun initviews() {
        _binding = UiHomeActivityMainBinding.inflate(layoutInflater,null,false)
    }

    private fun setupNavigationController() {
        navHostFragment = (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment)

        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        navHostFragment.findNavController().addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.landingFragment, R.id.statisticsFragment , R.id.profileFragment ->
                    binding.bottomNavigation.visibility = View.VISIBLE
                else -> binding.bottomNavigation.visibility = View.GONE
            }

            when(destination.id) {
                R.id.eyeTestingFragment, R.id.astigmatismTestingFragment, R.id.hyperopiaTestingFragment -> binding.appBarLayout.visibility = View.GONE
                else -> binding.appBarLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun setupAppBar() {
        val user = getSignedInUser()
        if (user != null) {
           binding.apply {
               tvToolbarName.text = user.displayName
               if(user.photoUrl != null)
               {
                   tvToolbarShortName.visibility = View.GONE
                   ivToolbarProfilePicture.visibility = View.VISIBLE
                   Glide.with(this@MainActivity).load(user.photoUrl).into(ivToolbarProfilePicture)
               } else {
                   tvToolbarShortName.visibility = View.VISIBLE
                   ivToolbarProfilePicture.visibility = View.GONE

                   val shortName = user.displayName?.substring(0,1) +
                           user.displayName?.indexOf(" ")?.plus(1)?.let { user.displayName?.get(it) }
                   tvToolbarShortName.text = shortName
               }
           }
        } else {
            binding.apply {
                tvToolbarName.text = "Guest User"
                tvToolbarShortName.visibility = View.VISIBLE
                ivToolbarProfilePicture.visibility = View.GONE
                tvToolbarShortName.text = "GU"
            }
        }
    }
}