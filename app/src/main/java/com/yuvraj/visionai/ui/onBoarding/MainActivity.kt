package com.yuvraj.visionai.ui.onBoarding

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
        inAppUpdate!!.checkUpdate()
        inAppUpdate!!.onStart()

        navHostFragment = (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment)
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

    override fun onResume() {
        super.onResume()
        inAppUpdate!!.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        inAppUpdate!!.onDestroy()
    }
}