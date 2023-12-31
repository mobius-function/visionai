package com.yuvraj.visionai.ui.home.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.face.Face
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentHomeLandingBinding
import com.yuvraj.visionai.service.cameraX.CameraManager
import com.yuvraj.visionai.service.faceDetection.FaceStatus

/**
 * A simple [Fragment] subclass.
 * Use the [LandingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LandingFragment : Fragment(R.layout.fragment_home_landing) {

    private var _binding: FragmentHomeLandingBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraManager: CameraManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  Initialize Python
        //  Python.start(AndroidPlatform(this.requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inflate the layout for this fragment
        initViews(view)
        createCameraManager()
        checkForPermission()
        clickableViews()
    }

    private fun initViews(view: View) {
        _binding = FragmentHomeLandingBinding.bind(view)

        // Display width of the graphic overlay on tvCamera
//        binding.tvFaceWidth.text = FaceContourGraphic.fac
    }

    private fun clickableViews() {

        binding.apply {
            tvStart.setOnClickListener {
                findNavController().navigate(R.id.action_landingFragment_to_eyeTestingFragment)
            }

            btnSwitchCamera.setOnClickListener {
                cameraManager.changeCameraSelector()
            }

            tvFaceWidth.setOnClickListener{
                binding.tvFaceWidth.text = binding.graphicOverlayFinder.width.toString()
            }
        }
    }

    private fun checkForPermission() {
        if (allPermissionsGranted()) {
            cameraManager.startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                cameraManager.startCamera()
            } else {
                Toast.makeText(requireActivity(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()

                requireActivity().finish()
            }
        }
    }

    private fun createCameraManager() {
        cameraManager = CameraManager(
            requireActivity(),
            binding.previewViewFinder,
            this,
            binding.graphicOverlayFinder,
            ::processPicture,
            ::updateTVFaceWidth
        )
    }


    private fun processPicture(faceStatus: FaceStatus) {
        Log.e("facestatus","This is it ${faceStatus.name}")
//        tvFaceWidth.text
//       when(faceStatus){}
    }

    private fun updateTVFaceWidth(face: Face) {
        binding.tvFaceWidth.text = "Face Width: ${face.boundingBox.width()}"
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireActivity().baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
    }

//    private fun initPython() {
//        val python = Python.getInstance()
//        val pythonFile = python.getModule("realtime_distance_calculator")
//        pythonFile.callAttr("mainf", binding.tvLandingPage, binding.ivImage)
//    }
}