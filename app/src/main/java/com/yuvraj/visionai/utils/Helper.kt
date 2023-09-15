package com.yuvraj.visionai.utils

class Helper {

    fun focalLengthFinder(
        measuredDistance: Double,
        realWidth: Double,
        widthInRfImage: Double
    ): Double {
        return (widthInRfImage * measuredDistance) / realWidth
    }

    fun distanceFinder(
        focalLength : Double,
        realFaceWidth : Double,
        faceWidthInFrame : Double): Double {
        return (realFaceWidth * focalLength) / faceWidthInFrame
    }
}