package com.yuvraj.visionai.data

import com.yuvraj.visionai.model.EyeTests
import com.yuvraj.visionai.utils.Constants

object EyeTestMenuList {
    fun getEyeTestMenuList(): List<EyeTests> {
        return listOf(
            EyeTests(
                Constants.MYOPIA,
                "Myopia Test"
            ),

            EyeTests(
                Constants.HYPEROPIA,
                "Hyperopia Test"
            ),

            EyeTests(
                Constants.ASTIGMATISM,
                "Astigmatism Test"
            ),

            EyeTests(
                Constants.DRY_EYE,
                "Dry Eye Test"
            ),

            EyeTests(
                Constants.ML_MODEL,
                "Ml Model Test"
            ),

            EyeTests(
                Constants.LOGOUT,
                "Log Out"
            )
        )
    }
}
