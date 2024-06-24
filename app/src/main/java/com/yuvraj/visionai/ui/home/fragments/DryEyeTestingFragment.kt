package com.yuvraj.visionai.ui.home.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentHomeDryEyeTestingBinding
import com.yuvraj.visionai.ui.home.MainActivity
import com.yuvraj.visionai.utils.DebugTags.CAMERA_X
import com.yuvraj.visionai.utils.DebugTags.FACE_DETECTION
import com.yuvraj.visionai.utils.helpers.Permissions.allPermissionsGranted
import kotlinx.coroutines.delay
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DryEyeTestingFragment : Fragment(R.layout.fragment_home_dry_eye_testing) {

    private var _binding: FragmentHomeDryEyeTestingBinding? = null
    private val binding get() = _binding!!

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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inflate the layout for this fragment
        initViews(view)
        clickableViews()
    }

    fun initViews(view: View) {
        _binding = FragmentHomeDryEyeTestingBinding.bind(view)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            startPermissionsRequest(REQUIRED_PERMISSIONS)
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
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())

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
                Log.e(CAMERA_X, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireActivity()))
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

                            if(face.leftEyeOpenProbability!! in 0.35..0.65 ) {
                                binding.apply {
                                    leftEyePartialBlinkCounter += 1
                                    tvLeftEye.text = "Partial Blink Counter: $leftEyePartialBlinkCounter \n" +
                                        "Full Blink Counter: $leftEyeFullBlinkCounter"

                                    if (leftEyePartialBlinkCounter == 10) {
                                        Toast.makeText(
                                            requireActivity(),
                                            "You Have Dry Eye in left eye",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else if(face.rightEyeOpenProbability!! in 0.35..0.65 ) {
                                binding.apply {
                                    rightEyePartialBlinkCounter += 1
                                    tvRightEye.text = "Partial Blink Counter: $rightEyePartialBlinkCounter \n" +
                                            "Full Blink Counter: $rightEyeFullBlinkCounter"

                                    if (rightEyePartialBlinkCounter == 10) {
                                        Toast.makeText(
                                            requireActivity(),
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

                                Log.e(FACE_DETECTION, "does not blink")
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
        ContextCompat.checkSelfPermission(requireActivity().baseContext, it) ==
                PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            Log.d("DEBUG", "${it.key} = ${it.value}")
        }

        if (requireActivity().allPermissionsGranted()) {
            // Navigate to MainActivity
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
        } else {
            // continue asking for permissions
        }
    }

    private fun startPermissionsRequest(permissions: Array<String>) {
        requestMultiplePermissions.launch(permissions)
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