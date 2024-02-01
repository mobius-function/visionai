package com.yuvraj.visionai.ui.home.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.mlkit.vision.face.Face
import com.yuvraj.visionai.R
import com.yuvraj.visionai.databinding.FragmentHomeLandingBinding
import com.yuvraj.visionai.firebase.Authentication.Companion.signOutUser
import com.yuvraj.visionai.service.cameraX.CameraManager
import com.yuvraj.visionai.service.faceDetection.FaceStatus
import com.yuvraj.visionai.ui.onBoarding.MainActivity
import com.yuvraj.visionai.utils.helpers.DistanceHelper

/**
 * A simple [Fragment] subclass.
 * Use the [LandingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LandingFragment : Fragment(R.layout.fragment_home_landing) {

    private var _binding: FragmentHomeLandingBinding? = null
    private val binding get() = _binding!!

//    private lateinit var cameraManager: CameraManager

    val focalLengthFound : Double = 50.0
    val realFaceWidth : Double = 14.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  Initialize Python
        //  Python.start(AndroidPlatform(this.requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inflate the layout for this fragment
        initViews(view)
//        createCameraManager()
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

//            btnSwitchCamera.setOnClickListener {
//                cameraManager.changeCameraSelector()
//            }

            btnMyopiaTesting.setOnClickListener {
                findNavController().navigate(R.id.action_landingFragment_to_eyeTestingFragment)
            }

            btnHyperopiaTesting.setOnClickListener {
                findNavController().navigate(R.id.action_homeLandingFragment_to_hyperopiaTestingFragment)
            }

            btnAstigmatismTesting.setOnClickListener {
                findNavController().navigate(R.id.action_homeLandingFragment_to_astigmatismTestingFragment)
            }

            btnTesting.setOnClickListener {
                findNavController().navigate(R.id.action_landingFragment_to_testingFragment)
            }

            btnLogOut.setOnClickListener {
                signOutUser()

                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)

                requireActivity().finish()
            }
        }
    }

    private fun checkForPermission() {
        if (allPermissionsGranted()) {
//            cameraManager.startCamera()
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
//                cameraManager.startCamera()
                Toast.makeText(requireActivity(),
                    "All Permissions are granted by the user.",
                    Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireActivity(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()

                requireActivity().finish()
            }
        }
    }

//    private fun createCameraManager() {
//        cameraManager = CameraManager(
//            requireActivity(),
//            binding.previewViewFinder,
//            this,
//            binding.graphicOverlayFinder,
//            ::processPicture,
//            ::updateTVFaceWidth
//        )
//    }


//    private fun processPicture(faceStatus: FaceStatus) {
//        Log.e("facestatus","This is it ${faceStatus.name}")
////        tvFaceWidth.text
////       when(faceStatus){}
//    }
//
//    private fun updateTVFaceWidth(face: Face) {
//        val faceWidth : Int = DistanceHelper.pixelsToDp(face.boundingBox.width()).toInt()
//        var distance = 0.0
//
//        if(faceWidth != 0) {
//            distance = DistanceHelper.distanceFinder(
//                focalLengthFound,
//                realFaceWidth,
//                faceWidth.toDouble()
//            )
//        }
//
//        binding.tvFaceWidth.text = "Current Distance: ${distance*10}"
//    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireActivity().baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
    }
}