package com.yuvraj.visionai.model

data class EyePower (
    var leftEyePower : Float = 0f,
    var rightEyePower : Float = 0f
) {
    // No-argument constructor is implicitly added by the Kotlin compiler
    constructor() : this(0f, 0f)
}