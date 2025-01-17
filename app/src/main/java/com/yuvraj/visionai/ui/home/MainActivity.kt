package com.yuvraj.visionai.ui.home

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessaging
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.UiHomeActivityMainBinding
import com.yuvraj.visionai.firebase.Authentication
import com.yuvraj.visionai.firebase.Authentication.Companion.getSignedInUser
import com.yuvraj.visionai.service.autoUpdater.InAppUpdate
import com.yuvraj.visionai.ui.onBoarding.MainActivity
import com.yuvraj.visionai.utils.Constants.FIRST_USE_AFTER_LOGIN
import com.yuvraj.visionai.utils.Constants.USER_DETAILS
import com.yuvraj.visionai.utils.DebugTags.FIREBASE_PUSH_NOTIFICATION
import com.yuvraj.visionai.utils.ScreenUtils.hideSystemUI
import com.yuvraj.visionai.utils.clients.AlertDialogBox.Companion.showInputBoxForFocalLength
import com.yuvraj.visionai.utils.clients.NotificationHelper.scheduleRegularNotification
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.initiateAllInOneEyeTestMode
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.setAllInOneEyeTestMode
import dagger.hilt.android.AndroidEntryPoint
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.getkeepsafe.taptargetview.TapTargetView
import com.yuvraj.visionai.utils.Constants.DEBUG_MODE
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.hasOpenedAppFirstTime
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.isDebugMode
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.openedAppFirstTime


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: UiHomeActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var navHostFragment: NavHostFragment

    //In-App Update manager
    private var inAppUpdate: InAppUpdate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        setContentView(binding.root)
        hideSystemUI()
        setupAppBar()
        setupNavigationController()
        clickableViews()

        if(hasOpenedAppFirstTime() || isDebugMode()) {
            showShowcase()
        }
    }


    fun initViews() {
        _binding = UiHomeActivityMainBinding.inflate(layoutInflater,null,false)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        setAllInOneEyeTestMode(false)

        val userSharedPreferences = this.getSharedPreferences(USER_DETAILS, MODE_PRIVATE)
        val firstUseAfterLogin = userSharedPreferences.getBoolean(FIRST_USE_AFTER_LOGIN, true)

        if(firstUseAfterLogin){
//            val sharedPreferences = this.getSharedPreferences(NOTIFICATION_PREFERENCES, MODE_PRIVATE)
//            val isEyeTestReminderEnabled = sharedPreferences.getBoolean(EYE_TEST_REMINDER, true)
        }


        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                // this fail
                if (!task.isSuccessful) {
                    Log.d(
                        FIREBASE_PUSH_NOTIFICATION,
                        "Fetching FCM registration token failed",
                        task.exception
                    )
                    return@addOnCompleteListener
                }
            }

        //In-App Update Initializer
        inAppUpdate = InAppUpdate(this@MainActivity)
        inAppUpdate!!.checkUpdate()
        inAppUpdate!!.onStart()
    }

    private fun clickableViews() {
        binding.apply {
            ivToolbarProfilePicture.setOnClickListener {
                navHostFragment.findNavController().navigate(R.id.profileFragment)
            }

            btnEyeTest.setOnClickListener {
                initiateAllInOneEyeTestMode()
                navHostFragment.findNavController().navigate(R.id.eyeTestingFragment)
            }
        }
    }

    private fun setupNavigationController() {
        navHostFragment = (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment)

        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        binding.bottomNavigationView.background = null

        // Disable the middle item of the bottom navigation view
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false

        navHostFragment.findNavController().addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.landingFragment,
                R.id.statisticsFragment ,
                R.id.notificationsFragment,
                R.id.chatBotFragment -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                    binding.btnEyeTestDummy.visibility = View.VISIBLE
                }

                else -> {
                    binding.bottomNavigation.visibility = View.GONE
                    binding.btnEyeTestDummy.visibility = View.GONE
                }
            }

            when(destination.id) {
                R.id.eyeTestingFragment,
                R.id.astigmatismTestingFragment,
                R.id.hyperopiaTestingFragment,
                R.id.profileFragment -> binding.appBarLayout.visibility = View.GONE

                else -> binding.appBarLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun setupAppBar() {
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

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
                           user.displayName?.indexOf(" ")?.plus(1)?.let {
                               user.displayName?.get(it)
                           }
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

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                    imm?.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
    
    private fun logOutUser() {
        Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show()
        Authentication.signOutUser()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    fun showShowcase() {
        val sequence = TapTargetSequence(this)
            .targets(
//                TapTarget.forView(
//                    binding.ivToolbarProfilePictureDummy, // Replace with actual menu item ID
//                    "Menu Item 1",
//                    "This is the first menu item."
//                )
//                    .outerCircleColor(R.color.theme_primary)
//                    .targetCircleColor(android.R.color.black)
//                    .textColor(android.R.color.black)
//                    .drawShadow(true)
//                    .cancelable(true),

                TapTarget.forView(
                    binding.bottomNavigationView.findViewById(R.id.landingFragment), // Replace with actual ID
                    "Home Page",
                    "Get started with different options here!"
                )
                    .outerCircleColor(R.color.theme_primary)
                    .targetCircleColor(android.R.color.black)
                    .textColor(android.R.color.black)
                    .drawShadow(true)
                    .cancelable(true),

                TapTarget.forView(
                    binding.bottomNavigationView.findViewById(R.id.notificationsFragment), // Replace with actual ID
                    "Alerts & Notifications",
                    "Get notified about your eye health and other important updates."
                )
                    .outerCircleColor(R.color.theme_primary)
                    .targetCircleColor(android.R.color.black)
                    .textColor(android.R.color.black)
                    .drawShadow(true)
                    .cancelable(true),

                TapTarget.forView(
                    binding.btnEyeTestDummy,
                    "Eye Test Button",
                    "Start your eye test by clicking this button."
                )
                    .outerCircleColor(R.color.theme_primary)
                    .targetCircleColor(android.R.color.black)
                    .textColor(android.R.color.black)
                    .drawShadow(false)
                    .cancelable(true),

                TapTarget.forView(
                    binding.bottomNavigationView.findViewById(R.id.statisticsFragment), // Replace with actual ID
                    "Eye Test Reports",
                    "View your eye test reports and stats."
                )
                    .outerCircleColor(R.color.theme_primary)
                    .targetCircleColor(android.R.color.black)
                    .textColor(android.R.color.black)
                    .drawShadow(true)
                    .cancelable(true),

                TapTarget.forView(
                    binding.bottomNavigationView.findViewById(R.id.chatBotFragment), // Replace with actual ID
                    "Chat Assistant",
                    "Get help from our chat assistant, Dr Vision GPT for any queries."
                )
                    .outerCircleColor(R.color.theme_primary)
                    .targetCircleColor(android.R.color.black)
                    .textColor(android.R.color.black)
                    .drawShadow(true)
                    .cancelable(true)
            )
            .listener(object : TapTargetSequence.Listener {
                override fun onSequenceFinish() {
                    // Showcase finished
                    openedAppFirstTime(false)
                }

                override fun onSequenceStep(lastTarget: TapTarget, targetClicked: Boolean) {
                    // Called when each target is clicked
                    Log.d("Debug TapTargetView", "Clicked on " + lastTarget + "Target Clicked: $targetClicked")
                }

                override fun onSequenceCanceled(lastTarget: TapTarget) {
                    // Showcase canceled
                    openedAppFirstTime(false)
                }
            })

        sequence.start()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_appbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logOut -> logOutUser()
            R.id.focalLength -> showInputBoxForFocalLength()
            R.id.mlModel -> navHostFragment.findNavController().navigate(R.id.testingFragment)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        inAppUpdate!!.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        setAllInOneEyeTestMode(false)
        inAppUpdate!!.onDestroy()
        scheduleRegularNotification()
        super.onDestroy()
    }
}