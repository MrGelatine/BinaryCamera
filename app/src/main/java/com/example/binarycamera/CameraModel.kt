package com.example.binarycamera

import android.graphics.Rect
import android.hardware.Camera
import android.hardware.Camera.AutoFocusCallback
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField
import androidx.lifecycle.Observer
import com.example.binarycamera.MainActivity.Companion.TAG
import java.math.RoundingMode


class CameraModel(val cameraActivity:MainActivity): BaseObservable() {
    private val FOCUS_AREA_SIZE = 100
    var photoVisibility = ObservableField(View.VISIBLE)
    var declineAcceptVisibility = ObservableField(View.GONE)
    var fileSizeVisibility = ObservableField(View.GONE)
    var fileSize= ObservableField<String>()


    fun makePhoto() {
        cameraActivity.focus.value = true
    }

    fun Decline(){
        cameraActivity.onResume()
        photoVisibility.set(View.VISIBLE)
        declineAcceptVisibility.set(View.GONE)
        cameraActivity.preview = false
    }

    fun Accept(){
        cameraActivity.packPhoto()
        cameraActivity.onResume()
        photoVisibility.set(View.VISIBLE)
        declineAcceptVisibility.set(View.GONE)
        cameraActivity.preview = false
    }
    fun showPreview() {
        if (!cameraActivity.preview) {
            fileSizeVisibility.set(View.VISIBLE)
            fileSize.set("${(cameraActivity.buffSize / 8000.0).toBigDecimal().setScale(2, RoundingMode.UP)}КB -> ${(cameraActivity.compreseBuffSize / 8000.0).toBigDecimal().setScale(2, RoundingMode.UP)}КB")
            cameraActivity.unpackPhoto()
            photoVisibility.set(View.GONE)
            cameraActivity.preview = true
        } else {
            fileSizeVisibility.set(View.GONE)
            photoVisibility.set(View.VISIBLE)
            declineAcceptVisibility.set(View.GONE)
            cameraActivity.preview = false
        }
    }


    private fun calculateFocusArea(x: Float, y: Float): Rect? {
        val left = (x * (2000.0/cameraActivity.cWidth)).toInt() - 1000
        val top = (y * (2000.0/cameraActivity.cHeight)).toInt() - 1000
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