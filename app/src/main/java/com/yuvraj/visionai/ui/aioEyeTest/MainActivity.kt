package com.yuvraj.visionai.ui.aioEyeTest

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.UiAioEyeTestActivityMainBinding
import com.yuvraj.visionai.databinding.UiHomeActivityMainBinding
import com.yuvraj.visionai.utils.DebugTags
import com.yuvraj.visionai.utils.ScreenUtils.hideSystemUI
import kotlinx.coroutines.delay
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var _binding: UiAioEyeTestActivityMainBinding
    private val binding get() = _binding!!

    private lateinit var navHostFragment: NavHostFragment

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraControl: CameraControl

    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var cameraSelectorOption = CameraSelector.DEFAULT_FRONT_CAMERA

    private var leftEyePartialBlinkCounter: Int = 0
    private var rightEyePartialBlinkCounter: Int = 0
    private var leftEyeFullBlinkCounter: Int = 0
    private var rightEyeFullBlinkCounter: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        setContentView(binding.root)
        hideSystemUI()
        clickableViews()
    }

    private fun initViews() {
        _binding = UiAioEyeTestActivityMainBinding.inflate(layoutInflater,null,false)

        navHostFragment = (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment)
        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listeners for take photo and video capture buttons
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun clickableViews() {
        binding.apply {

        }
    }

    private var highAccuracyOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()

    val faceDetector = FaceDetection.getClient(highAccuracyOpts)


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, YourImageAnalyzer())
                }

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                val camera=cameraProvider.bindToLifecycle(
                    this,
                    cameraSelectorOption,
                    preview,
                    imageCapture,
                    imageAnalyzer
                )

                cameraControl = camera.cameraControl

            } catch(exc: Exception) {
                Log.e(DebugTags.CAMERA_X, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }


    inner class YourImageAnalyzer : ImageAnalysis.Analyzer {

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
                            if(face.leftEyeOpenProbability!! in 0.4..0.7 ) {
                                binding.apply {
                                    leftEyePartialBlinkCounter += 1
                                    tvLeftEye.text = "Partial Blink Counter: $leftEyePartialBlinkCounter \n" +
                                            "Full Blink Counter: $leftEyeFullBlinkCounter"

                                    if (leftEyePartialBlinkCounter == 10) {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "You Have Dry Eye in left eye",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else if(face.rightEyeOpenProbability!! in 0.4..0.7 ) {
                                binding.apply {
                                    rightEyePartialBlinkCounter += 1
                                    tvLeftEye.text = "Partial Blink Counter: $rightEyePartialBlinkCounter \n" +
                                            "Full Blink Counter: $rightEyeFullBlinkCounter"

                                    if (rightEyePartialBlinkCounter == 10) {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "You Have Dry Eye in right eye",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else if(face.leftEyeOpenProbability!! < 0.3){
                                binding.tvLeftEyeFB.text="FULL BLINK"
                                leftEyeFullBlinkCounter += 1

                                binding.tvLeftEye.text = "Partial Blink Counter: $leftEyePartialBlinkCounter \n" +
                                        "Full Blink Counter: $leftEyeFullBlinkCounter"
                            } else if(face.rightEyeOpenProbability!! < 0.3){
                                binding.tvRightEyeFB.text="FULL BLINK"
                                rightEyeFullBlinkCounter += 1

                                binding.tvRightEye.text = "Partial Blink Counter: $rightEyePartialBlinkCounter \n" +
                                        "Full Blink Counter: $rightEyeFullBlinkCounter"
                            } else {
                                binding.tvLeftEyeFB.text="DOES NO BLINK"
                                binding.tvRightEyeFB.text="DOES NO BLINK"

                                Log.e(DebugTags.FACE_DETECTION, "does not blink")
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

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this.baseContext, it) ==
                PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                this.finish()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}