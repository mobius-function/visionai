package com.yuvraj.visionai.model

data class EyeTestResult(
    val plusPowerRightEye: Float = 0f,
    val plusPowerLeftEye: Float = 0f,
    val minusPowerRightEye: Float = 0f,
    val minusPowerLeftEye: Float = 0f,
    val astigmatismResult: Boolean = false,
    val dryEyeResult: Boolean = false,
    val jaundiceResult: Boolean = false
)

