package com.example.binarycamera

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.binarycamera.databinding.ActivityMainBinding
import org.opencv.android.OpenCVLoader


const val REQUEST_CODE_PERMISSIONS = 111
val REQUIRED_PERMISSIONS = arrayOf(
    CAMERA,
    WRITE_EXTERNAL_STORAGE,
    READ_EXTERNAL_STORAGE)

class MainActivity : AppCompatActivity(){
    private lateinit var mainBinding : ActivityMainBinding
    private lateinit var navGraph: NavController

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (allPermissionsGranted()) {
            checkOpenCV(this)
            mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
            val viewModel: GalleryViewModel by viewModels()
            viewModel.manager = supportFragmentManager
            navGraph = Navigation.findNavController(this, R.id.fragmentContainerView)
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS )
        }



    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                checkOpenCV(baseContext)
                mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
                val viewModel: GalleryViewModel by viewModels()
                viewModel.manager = supportFragmentManager
                navGraph = Navigation.findNavController(this, R.id.fragmentContainerView)
            } else {
                    CameraFragment.shortMsg(
                        baseContext,
                        CameraFragment.PERMISSION_NOT_GRANTED
                    )
                finish()
            }
        }
    }
    fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
    private fun checkOpenCV(context: Context) {
        if (OpenCVLoader.initDebug()) {
            CameraFragment.shortMsg(context, CameraFragment.OPENCV_SUCCESSFUL)
            CameraFragment.lgd("OpenCV started...")
        } else {
            CameraFragment.lge(CameraFragment.OPENCV_PROBLEM)
        }
    }

}
