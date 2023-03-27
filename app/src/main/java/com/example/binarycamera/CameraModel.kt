package com.example.binarycamera

import android.view.View
import androidx.databinding.*


class CameraModel(val camera:MainActivity): BaseObservable() {
    var photoVisibility = ObservableField<Int>(View.VISIBLE)
    var declineAcceptVisibility = ObservableField<Int>(View.GONE)

    fun makePhoto(){
        camera.preview = false
        camera.packPhoto()
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
        camera.unpackPhoto()
        camera.onResume()
        photoVisibility.set(View.VISIBLE)
        declineAcceptVisibility.set(View.GONE)
        camera.preview = false
    }
    fun Preview(){
        if(camera.preview) {
            photoVisibility.set(View.VISIBLE)
            declineAcceptVisibility.set(View.GONE)
            camera.preview = false
        }else if(camera.photoMat != null){
            photoVisibility.set(View.GONE)
            declineAcceptVisibility.set(View.GONE)
            camera.preview = true
        }
    }
}