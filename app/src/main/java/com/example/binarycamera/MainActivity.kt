package com.example.binarycamera

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.Camera.AutoFocusMoveCallback
import android.hardware.camera2.CameraCharacteristics
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.example.binarycamera.databinding.ActivityMainBinding
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.JavaCameraView
import org.opencv.android.OpenCVLoader
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_MEAN_C
import org.opencv.imgproc.Imgproc.COLOR_RGB2GRAY
import org.opencv.imgproc.Imgproc.THRESH_BINARY
import org.opencv.imgproc.Imgproc.adaptiveThreshold
import org.opencv.imgproc.Imgproc.cvtColor
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.util.zip.Deflater
import java.util.zip.Inflater


const val REQUEST_CODE_PERMISSIONS = 111
val REQUIRED_PERMISSIONS = arrayOf(
    CAMERA,
    WRITE_EXTERNAL_STORAGE,
    READ_EXTERNAL_STORAGE)

class MainActivity : AppCompatActivity(){
    private lateinit var mainBinding : ActivityMainBinding
    private lateinit var navGraph: NavController
    /*
    var focus: MutableLiveData<Boolean> = MutableLiveData()


    lateinit var imageMat: Mat

    private lateinit var viewModel: CameraModel
    private val viewFinder by lazy { findViewById<JavaCameraView>(R.id.cameraView)}
    private var coder = Deflater()
    private var decoder = Inflater()
    var preview = false
    private lateinit var curFrame:Mat
    //Preview Content
    private lateinit var photoMat:Mat
    var buffSize:Int = 0
    var compreseBuffSize:Int = 0
    var cWidth = -1
    var cHeight = -1
    lateinit var mCamera: Camera
    private fun isExternalStorageReadOnly(): Boolean {
        val extStorageState = Environment.getExternalStorageState()
        return if (Environment.MEDIA_MOUNTED_READ_ONLY == extStorageState) {
            true
        } else false
    }
    private fun isExternalStorageAvailable(): Boolean {
        val extStorageState = Environment.getExternalStorageState()
        return if (Environment.MEDIA_MOUNTED == extStorageState) {
            true
        } else false
    }

    private fun checkOpenCV(context: Context) {
        if (OpenCVLoader.initDebug()) {
            shortMsg(context, OPENCV_SUCCESSFUL)
            lgd("OpenCV started...")
        } else {
            lge(OPENCV_PROBLEM)
        }
    }
    private val mAutoFocusTakePictureCallback =
        Camera.AutoFocusCallback { success, camera ->
            if (success) {
                focus.value = false
                Log.i("tap_to_focus", "success!")
                camera.cancelAutoFocus()
                onPause()
                viewModel.photoVisibility.set(View.GONE)
                viewModel.declineAcceptVisibility.set(View.VISIBLE)
                lgd("Focus!")
            }else{
                camera.cancelAutoFocus()
                camera.setAutoFocusMoveCallback(AutoFocusMoveCallback { start, camera ->
                    if(!start){
                        focus.value = false
                        Log.i("tap_to_focus", "success!")
                        camera.cancelAutoFocus()
                        onPause()
                        viewModel.photoVisibility.set(View.GONE)
                        viewModel.declineAcceptVisibility.set(View.VISIBLE)
                        lgd("Focus!")
                    }

                })
            }
        }
    companion object {
        val TAG = "MYLOG " + MainActivity::class.java.simpleName
        public fun lgd(s: String) = Log.d(TAG, s)
        fun lge(s: String) = Log.e(TAG, s)
        fun lgi(s: String) = Log.i(TAG, s)

        fun shortMsg(context: Context, s: String) =
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show()

        // messages:
        const val OPENCV_SUCCESSFUL = "OpenCV Loaded Successfully!"
        const val OPENCV_PROBLEM = "There's a problem in OpenCV."
        const val PERMISSION_NOT_GRANTED = "Permissions not granted by the user."

    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        navGraph = Navigation.findNavController(this, R.id.fragmentContainerView)

    }
    /*
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
        mCamera = viewFinder.mCamera
        mCamera.parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)
        cWidth = mainBinding.cameraView.width
        cHeight = mainBinding.cameraView.height
        val nameObserver = Observer<Boolean> { f ->
            if(focus.value!!) {
                mCamera.autoFocus(mAutoFocusTakePictureCallback)
            }
        }
        focus.observe(this,nameObserver)
    }

    override fun onCameraViewStopped() {
        imageMat.release()
    }

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat {
        if(!preview) {
            imageMat = inputFrame!!.rgba()
            cvtColor(imageMat, imageMat, COLOR_RGB2GRAY);
            adaptiveThreshold(imageMat, imageMat, 255.0, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 15,
                40.0);
            curFrame = imageMat.clone()
            return imageMat
        }
        else{
            return photoMat
        }
    }
    fun packPhoto(){
        //Transform frame to bytes array
        val return_buff = ByteArray(
            (curFrame.total().toInt())
        )
        curFrame.get(0, 0, return_buff)
        buffSize = return_buff.size
        //Store bytes array to file
        storagePhoto(return_buff)
    }

    fun storagePhoto(data:ByteArray){
        //Data encrypting
        coder = Deflater()
        val codedBuff = ByteArray(buffSize)
        coder.setInput(data)
        coder.finish()
        compreseBuffSize = coder.deflate(codedBuff)
        coder.end()
        //Load data to file
        try {
            val dataWriter = FileOutputStream(File(getExternalFilesDir("BinaryStorage"), "output.dat"))
            val wrap =  DataOutputStream(dataWriter)
            wrap.writeInt(curFrame.size().width.toInt())
            wrap.writeInt(curFrame.size().height.toInt())
            wrap.writeInt(compreseBuffSize)
            wrap.writeInt(buffSize)
            dataWriter.write(codedBuff)
            dataWriter.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun unpackPhoto(){
            //Load bytes from file
        val dataReader = FileInputStream(File(getExternalFilesDir("BinaryStorage"), "output.dat"))
        val scanner = DataInputStream(dataReader)
        val height = scanner.readInt().toDouble()
        val width = scanner.readInt().toDouble()
        val prevSize = Size(height,width)
        val dada = DataInputStream(dataReader)
        val compBuffSize = dada.readInt()
        val bSize = dada.readInt()
            var res = ByteArray(compBuffSize)
            dataReader.read(res)

            //Unpack bytes
            decoder = Inflater()
            decoder.setInput(res, 0, compBuffSize)
            val result = ByteArray(bSize)
            decoder.inflate(result)
            decoder.end()

            var prevMat = Mat(prevSize,CvType.CV_8UC1)
            prevMat.put(0,0, result)
            photoMat = prevMat

    }
    */
}
