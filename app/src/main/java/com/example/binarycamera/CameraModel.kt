package com.example.binarycamera

import android.graphics.Rect
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField
import java.math.RoundingMode


class CameraModel(val cameraFragment:CameraFragment): BaseObservable() {
    private val FOCUS_AREA_SIZE = 100
    var photoVisibility = ObservableField(View.VISIBLE)
    var declineAcceptVisibility = ObservableField(View.GONE)
    var fileSizeVisibility = ObservableField(View.GONE)
    var fileSize= ObservableField<String>()


    fun makePhoto() {
        cameraFragment.focus.value = true
    }

    fun Decline(){
        cameraFragment.onResume()
        photoVisibility.set(View.VISIBLE)
        declineAcceptVisibility.set(View.GONE)
        cameraFragment.preview = false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun Accept(){
        cameraFragment.packPhoto()
        cameraFragment.onResume()
        photoVisibility.set(View.VISIBLE)
        declineAcceptVisibility.set(View.GONE)
        cameraFragment.preview = false
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


    private fun calculateFocusArea(x: Float, y: Float): Rect? {
        val left = (x * (2000.0/cameraFragment.cWidth)).toInt() - 1000
        val top = (y * (2000.0/cameraFragment.cHeight)).toInt() - 1000
        return Rect(left, top, clamp(left + FOCUS_AREA_SIZE,-1000, 1000), clamp(top + FOCUS_AREA_SIZE,-1000, 1000))
    }

    private fun clamp(x: Int, min: Int, max: Int): Int {
        if (x > max) {
            return max
        }
        return if (x < min) {
            min
        } else x
    }
}