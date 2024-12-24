package com.yuvraj.visionai.data

import com.yuvraj.visionai.model.EyeTests
import com.yuvraj.visionai.utils.Constants
import com.yuvraj.visionai.utils.Constants.ABOUT_US
import com.yuvraj.visionai.utils.Constants.USER_PROFILE
import com.yuvraj.visionai.utils.Constants.USER_SUBSCRIPTION

object EyeTestMenuList {
    fun getEyeTestMenuList(): List<EyeTests> {
        return listOf(
//            EyeTests(
//                Constants.MYOPIA,
//                "Myopia Test"
//            ),
//
//            EyeTests(
//                Constants.HYPEROPIA,
//                "Hyperopia Test"
//            ),
//
//            EyeTests(
//                Constants.ASTIGMATISM,
//                "Astigmatism Test"
//            ),
//
//            EyeTests(
//                Constants.DRY_EYE,
//                "Dry Eye Test"
//            )

            EyeTests(
                USER_PROFILE,
                "User Profile"
            ),

            EyeTests(
                USER_SUBSCRIPTION,
                "User Subscription"
            ),

            EyeTests(
                Constants.GET_STARTED,
                "Get Started"
            ),

            EyeTests(
                ABOUT_US,
                "About Us"
            ),

            EyeTests(
                Constants.DEBUG_TOGGLE,
                "Debug Mode"
            )
        )
    }
}
