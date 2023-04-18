package com.example.binarycamera

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.binarycamera.databinding.ActivityMainBinding


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
        val viewModel: GalleryViewModel by viewModels()

        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel.manager = supportFragmentManager
        navGraph = Navigation.findNavController(this, R.id.fragmentContainerView)

    }

}
