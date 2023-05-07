package com.example.binarycamera

import android.view.View
import androidx.lifecycle.ViewModel
import org.opencv.core.Mat

class CameraViewModel: ViewModel() {
    var threshold:Double = 40.0
    var resolutionPos:Int=0
    var declineAcceptVisibility:Int= View.GONE
    var photoVisibility:Int= View.VISIBLE
    var focus:Boolean = false
    var pause:Boolean= false
    var preview:Boolean = false
    var savePauseFrame: Mat = Mat()
}