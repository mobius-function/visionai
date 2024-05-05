package com.yuvraj.visionai.service.cameraX

import android.annotation.SuppressLint
import android.media.FaceDetector
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.yuvraj.visionai.utils.DebugTags

abstract class DryEyeAnalyzer(
    private val onSuccessCallback: ((Float, Float) -> Unit)
) : ImageAnalysis.Analyzer {

    private var highAccuracyOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()

    private val faceDetector = FaceDetection.getClient(highAccuracyOpts)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        mediaImage?.let {

            val visionImage = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            faceDetector.process(visionImage)
                .addOnSuccessListener {
                        faces ->
                    faces.forEach { face ->
                        if(face.rightEyeOpenProbability != null && face.leftEyeOpenProbability != null) {
                            onSuccessCallback(
                                face.rightEyeOpenProbability!!,
                                face.leftEyeOpenProbability!!
                            )
                        } else {
                            Log.e(DebugTags.FACE_DETECTION, "Face probabilities are null")
                            onSuccessCallback(1.0f, 1.0f)
                        }
                    }
                    imageProxy.close()
                }
                .addOnFailureListener {
                    it.printStackTrace()
                }

        }
    }
}