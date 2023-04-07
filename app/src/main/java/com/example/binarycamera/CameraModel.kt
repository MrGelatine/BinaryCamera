package com.example.binarycamera

import android.view.View
import androidx.databinding.*
import java.math.RoundingMode


class CameraModel(val camera:MainActivity): BaseObservable() {
    var photoVisibility = ObservableField(View.VISIBLE)
    var declineAcceptVisibility = ObservableField(View.GONE)
    var fileSizeVisibility = ObservableField(View.GONE)
    var fileSize= ObservableField<String>()

    fun makePhoto(){
        camera.preview = true
        camera.onPause()
        photoVisibility.set(View.GONE)
        declineAcceptVisibility.set(View.VISIBLE)
    }

    fun Decline(){
        camera.onResume()
        photoVisibility.set(View.VISIBLE)
        declineAcceptVisibility.set(View.GONE)
        camera.preview = false
    }

    fun Accept(){
        camera.packPhoto()
        camera.onResume()
        photoVisibility.set(View.VISIBLE)
        declineAcceptVisibility.set(View.GONE)
        camera.preview = false

    }
    fun showPreview() {
        if (!camera.preview) {
            fileSizeVisibility.set(View.VISIBLE)
            fileSize.set("${(camera.buffSize / 8000.0).toBigDecimal().setScale(2, RoundingMode.UP)}КB -> ${(camera.compreseBuffSize / 8000.0).toBigDecimal().setScale(2, RoundingMode.UP)}КB")
            camera.unpackPhoto()
            photoVisibility.set(View.GONE)
            camera.preview = true
        } else {
            fileSizeVisibility.set(View.GONE)
            photoVisibility.set(View.VISIBLE)
            declineAcceptVisibility.set(View.GONE)
            camera.preview = false
        }
    }
}