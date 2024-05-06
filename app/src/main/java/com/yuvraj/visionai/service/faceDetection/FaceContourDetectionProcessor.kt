package com.yuvraj.visionai.service.faceDetection

import android.graphics.Rect
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.yuvraj.visionai.model.FaceStatus
import com.yuvraj.visionai.service.cameraX.BaseImageAnalyzer
import com.yuvraj.visionai.service.cameraX.GraphicOverlay
import com.yuvraj.visionai.utils.DebugTags.FACE_DETECTION
import java.io.IOException

class FaceContourDetectionProcessor(private val view: GraphicOverlay,
                                    private val onSuccessCallback: ((FaceStatus) -> Unit),
                                    private val onSuccessCallbackFace: ((Face, Float, Float) -> Unit)):
    BaseImageAnalyzer<List<Face>>() {

    private val realTimeOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
        .build()

    private var highAccuracyOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()

    private var currentFaceDetectionOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()

    private val detector = FaceDetection.getClient(currentFaceDetectionOpts)

    override val graphicOverlay: GraphicOverlay
        get() = view

    override fun detectInImage(image: InputImage): Task<List<Face>> {
        return detector.process(image)
    }

    override fun stop() {
        try {
            detector.close()
        } catch (e: IOException) {
            Log.e(FACE_DETECTION, "Exception thrown while trying to close Face Detector: $e")
        }
    }

    override fun onSuccess(
        results: List<Face>,
        graphicOverlay: GraphicOverlay,
        rect: Rect
    ) {
        graphicOverlay.clear()
        if (results.isNotEmpty()){
            results.forEach {
                val faceGraphic = FaceContourGraphic(graphicOverlay, it, rect
                    ,onSuccessCallback,onSuccessCallbackFace)
                graphicOverlay.add(faceGraphic)
            }
            graphicOverlay.postInvalidate()
        }else{
            onSuccessCallback(FaceStatus.NO_FACE)
            Log.e(FACE_DETECTION, "Face Detector failed.")
        }

    }

    override fun onFailure(e: Exception) {
        Log.e(FACE_DETECTION, "Face Detector failed. $e")
        onSuccessCallback(FaceStatus.NO_FACE)
    }
}