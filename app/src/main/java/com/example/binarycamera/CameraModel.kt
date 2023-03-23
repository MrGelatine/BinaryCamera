package com.example.binarycamera

import android.view.View
import androidx.databinding.*


class CameraModel(val camera:MainActivity): BaseObservable() {
    var photoVisibility = ObservableField<Int>(View.VISIBLE)
    var declineAcceptVisibility = ObservableField<Int>(View.GONE)

    fun makePhoto(){
        //camera.onPause()
        camera.packPhoto()
        photoVisibility.set(View.GONE)
        declineAcceptVisibility.set(View.VISIBLE)


    }

    fun Decline(){
        //camera.onResume()
        photoVisibility.set(View.VISIBLE)
        declineAcceptVisibility.set(View.GONE)
        camera.preview = false
    }

    fun Accept(){
        //camera.onResume()
        photoVisibility.set(View.VISIBLE)
        declineAcceptVisibility.set(View.GONE)
        camera.preview = false
    }
}