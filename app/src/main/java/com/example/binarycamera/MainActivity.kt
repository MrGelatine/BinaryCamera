package com.example.binarycamera

import android.Manifest.permission.*
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.WindowManager.LayoutParams.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.binarycamera.databinding.ActivityMainBinding
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.JavaCameraView
import org.opencv.android.OpenCVLoader
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Imgproc.*
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.util.zip.Deflater
import java.util.zip.Inflater


private const val REQUEST_CODE_PERMISSIONS = 111
private val REQUIRED_PERMISSIONS = arrayOf(
    CAMERA,
    WRITE_EXTERNAL_STORAGE,
    READ_EXTERNAL_STORAGE)

class MainActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener2 {

    private val context: Context = getApplicationContext()
    lateinit var imageMat: Mat
    private lateinit var mainBinding : ActivityMainBinding
    private lateinit var viewModel: CameraModel
    private val viewFinder by lazy { findViewById<JavaCameraView>(R.id.cameraView)}
    private lateinit var photoFrame: Mat
    private var coder = Deflater()
    private var decoder = Inflater()
    public var preview = false
    private lateinit var curFrame:Mat
    private lateinit var photoMat:Mat
    private fun checkOpenCV(context: Context) {
        if (OpenCVLoader.initDebug()) {
            shortMsg(context, OPENCV_SUCCESSFUL)
            lgd("OpenCV started...")
        } else {
            lge(OPENCV_PROBLEM)
        }
    }
    companion object {

        val TAG = "MYLOG " + MainActivity::class.java.simpleName
        fun lgd(s: String) = Log.d(TAG, s)
        fun lge(s: String) = Log.e(TAG, s)
        fun lgi(s: String) = Log.i(TAG, s)

        fun shortMsg(context: Context, s: String) =
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show()

        // messages:
        private const val OPENCV_SUCCESSFUL = "OpenCV Loaded Successfully!"
        private const val OPENCV_FAIL = "Could not load OpenCV!!!"
        private const val OPENCV_PROBLEM = "There's a problem in OpenCV."
        private const val PERMISSION_NOT_GRANTED = "Permissions not granted by the user."

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = CameraModel(this)

        mainBinding.camera = viewModel

        window.clearFlags(FLAG_FORCE_NOT_FULLSCREEN)
        window.setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN)
        window.addFlags(FLAG_KEEP_SCREEN_ON)
        // Request camera permissions
        if (allPermissionsGranted()) {
            checkOpenCV(this)
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        viewFinder.visibility = SurfaceView.VISIBLE
        viewFinder.setCameraIndex(
            CameraCharacteristics.LENS_FACING_FRONT)
        viewFinder.setCvCameraViewListener(this)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                checkOpenCV(this)
            } else {
                shortMsg(this, PERMISSION_NOT_GRANTED)
                finish()
            }
        }
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
    public override fun onPause() {
        super.onPause()
        viewFinder?.let { viewFinder.disableView() }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewFinder?.let { viewFinder.disableView() }
    }
    public override fun onResume() {
        super.onResume()
        viewFinder?.let { viewFinder.enableView() }
    }
    override fun onCameraViewStarted(width: Int, height: Int) {
        imageMat = Mat(width, height, CvType.CV_8UC4)
    }

    override fun onCameraViewStopped() {
        imageMat.release()
    }

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat {
        if(!preview) {
            imageMat = inputFrame!!.rgba()
            cvtColor(imageMat, imageMat, Imgproc.COLOR_RGB2GRAY);
            adaptiveThreshold(imageMat, imageMat, 255.0, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 15,
                40.0);
            curFrame = imageMat
            return imageMat
        }
        else{
            return photoMat
        }

    }
    fun packPhoto():Mat{
        val return_buff = ByteArray(
            (curFrame.total().toInt())
        )
        curFrame.get(0, 0, return_buff)

        //Encrypting data

        coder = Deflater()
        val output = ByteArray(return_buff.size)
        coder.setInput(return_buff)
        coder.finish()
        val compressedDataLength = coder.deflate(output)
        coder.end()

        decoder = Inflater()
        decoder.setInput(output, 0, compressedDataLength)
        val result = ByteArray(return_buff.size)
        val resultLength = decoder.inflate(result)
        decoder.end()

        var res = Mat(curFrame.size(),CvType.CV_8UC1)
        res.put(0,0, result)
        photoMat = res
        preview = true


        return res
    }
    fun storagePhoto(data:ByteArray){
        val dir = File(context.filesDir, "data")
        if(!dir.exists()){
            dir.mkdir();
        }
        try {
            val dataFile = File(dir, "output.dat")
            val dataWriter = FileOutputStream(dataFile)
            dataWriter.write(data)
            dataWriter.close()
            var res = ByteArray(dataFile.length().toInt())
            val dataReader = FileInputStream(dataFile);
            dataReader.read(res)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun unpackPhoto():ByteArray{
        val dir = File(context.filesDir, "data")
        try {
            val dataFile = File(dir, "output.dat")
            var res = ByteArray(dataFile.length().toInt())
            val dataReader = FileInputStream(dataFile);
            dataReader.read(res)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return res
    }
}
