package com.yuvraj.visionai.model

data class EyeTestResult(
    val id: String = "1/1/2021 12:00:00",
    val plusPowerRightEye: Float = 0f,
    val plusPowerLeftEye: Float = 0f,
    val minusPowerRightEye: Float = 0f,
    val minusPowerLeftEye: Float = 0f,
    val astigmatismResult: Boolean = false,
    val dryLeftEyeResult: Boolean = false,
    val dryRightEyeResult: Boolean = false,
    val jaundiceResult: Boolean = false
)

