package com.example.binarycamera

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField
import java.math.RoundingMode


class CameraData(val cameraFragment:CameraFragment): BaseObservable() {
    var photoVisibility = ObservableField(View.VISIBLE)
    var declineAcceptVisibility = ObservableField(View.GONE)
    var fileSizeVisibility = ObservableField(View.GONE)
    var fileSize= ObservableField<String>()



    @RequiresApi(Build.VERSION_CODES.O)
    fun makePhoto() {
        cameraFragment.focus.value = true
        cameraFragment.pause = true
        declineAcceptVisibility.set(View.VISIBLE)
        cameraFragment.cameraBinding.mainButton.visibility = View.GONE
        cameraFragment.packPhoto()
    }

    fun Decline(){
        cameraFragment.pause = false
        photoVisibility.set(View.VISIBLE)
        declineAcceptVisibility.set(View.GONE)
        cameraFragment.cameraBinding.mainButton.visibility = View.VISIBLE
        cameraFragment.preview = false
        cameraFragment.cameraBinding.sizeView.text = ""
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun Accept(){
        cameraFragment.saveToFile()
        cameraFragment.pause = false
        photoVisibility.set(View.VISIBLE)
        declineAcceptVisibility.set(View.GONE)
        cameraFragment.cameraBinding.mainButton.visibility = View.VISIBLE
        cameraFragment.preview = false
        cameraFragment.cameraBinding.sizeView.text = ""
    }
    fun showPreview() {
        if (!cameraFragment.preview) {
            fileSizeVisibility.set(View.VISIBLE)
            fileSize.set("${(cameraFragment.buffSize / 8000.0).toBigDecimal().setScale(2, RoundingMode.UP)}КB -> ${(cameraFragment.compreseBuffSize / 8000.0).toBigDecimal().setScale(2, RoundingMode.UP)}КB")
            cameraFragment.unpackPhoto()
            photoVisibility.set(View.GONE)
            cameraFragment.preview = true
        } else {
            fileSizeVisibility.set(View.GONE)
            photoVisibility.set(View.VISIBLE)
            declineAcceptVisibility.set(View.GONE)
            cameraFragment.preview = false
        }
    }
}