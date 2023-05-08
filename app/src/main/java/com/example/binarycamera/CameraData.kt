package com.example.binarycamera

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField
import java.math.RoundingMode


class CameraData(val cameraFragment:CameraFragment): BaseObservable() {
    var photoVisibility = ObservableField(cameraFragment.viewModel.photoVisibility)
    var declineAcceptVisibility = ObservableField(cameraFragment.viewModel.declineAcceptVisibility)
    var fileSizeVisibility = ObservableField(cameraFragment.viewModel.declineAcceptVisibility)
    var fileSize= ObservableField(cameraFragment.viewModel.sizeText)



    @RequiresApi(Build.VERSION_CODES.O)
    fun makePhoto() {
        cameraFragment.focus.value = true
        cameraFragment.pause = true
        declineAcceptVisibility.set(View.VISIBLE)
        photoVisibility.set(View.GONE)

        cameraFragment.viewModel.savePauseFrame = cameraFragment.savePauseFrame.clone()
        cameraFragment.viewModel.focus = true
        cameraFragment.viewModel.pause = true
        cameraFragment.viewModel.declineAcceptVisibility = View.VISIBLE
        cameraFragment.viewModel.photoVisibility = View.GONE


        cameraFragment.packPhoto()
    }

    fun Decline(){
        cameraFragment.pause = false
        photoVisibility.set(View.VISIBLE)
        declineAcceptVisibility.set(View.GONE)
        cameraFragment.preview = false
        cameraFragment.cameraBinding.sizeView.text = ""

        cameraFragment.viewModel.pause = false
        cameraFragment.viewModel.focus = false
        cameraFragment.viewModel.preview = false
        cameraFragment.viewModel.photoVisibility = View.VISIBLE
        cameraFragment.viewModel.declineAcceptVisibility = View.GONE
        cameraFragment.viewModel.sizeText = ""


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun Accept(){
        cameraFragment.saveToFile()
        cameraFragment.pause = false
        photoVisibility.set(View.VISIBLE)
        declineAcceptVisibility.set(View.GONE)
        cameraFragment.preview = false
        cameraFragment.cameraBinding.sizeView.text = ""

        cameraFragment.viewModel.pause = false
        cameraFragment.viewModel.focus = false
        cameraFragment.viewModel.preview = false
        cameraFragment.viewModel.photoVisibility = View.VISIBLE
        cameraFragment.viewModel.declineAcceptVisibility = View.GONE
        cameraFragment.viewModel.sizeText = ""
    }
}