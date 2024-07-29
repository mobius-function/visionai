package com.yuvraj.visionai.model

import java.util.Calendar

data class EyeTestResult(
    var id: String? = "",
    var plusPowerRightEye: Float? = 0f,
    var plusPowerLeftEye: Float? = 0f,
    var minusPowerRightEye: Float? = 0f,
    var minusPowerLeftEye: Float? = 0f,
    var astigmatismResult: Boolean? = false,
    var dryLeftEyeResult: Boolean? = false,
    var dryRightEyeResult: Boolean? = false,
    var jaundiceResult: Boolean? = false
) {
    // No-argument constructor is implicitly added by the Kotlin compiler
    constructor() : this(Calendar.getInstance().time.toString(), 0f, 0f, 0f, 0f, false, false, false, false)
}

