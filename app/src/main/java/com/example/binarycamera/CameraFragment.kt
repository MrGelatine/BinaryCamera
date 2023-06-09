package com.example.binarycamera

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.hardware.Camera
import android.hardware.camera2.CameraCharacteristics
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.binarycamera.databinding.FragmentCameraBinding
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.JavaCameraView
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.zip.Deflater
import java.util.zip.Inflater


class CameraFragment() : Fragment(), CameraBridgeViewBase.CvCameraViewListener2 {
    var focus: MutableLiveData<Boolean> = MutableLiveData()

    lateinit var imageMat: Mat
    var mInfoTextView: TextView? = null
    lateinit var cameraBinding : FragmentCameraBinding
    private lateinit var cameraInfo: CameraData
    private lateinit var viewFinder:JavaCameraView
    private var coder = Deflater()
    private var decoder = Inflater()
    var preview = false
    private lateinit var curFrame:Mat
    lateinit var savePauseFrame:Mat
    //Preview Content
    private lateinit var photoMat:Mat
    var buffSize:Int = 0
    lateinit var codedBuff:ByteArray
    var compreseBuffSize:Int = 0
    val viewModel: CameraViewModel by activityViewModels()
    var name = ""
    var pause = false
    lateinit var mCamera: Camera
    lateinit var spinnerAdapter:ArrayAdapter<String>
    private val mAutoFocusTakePictureCallback =
        Camera.AutoFocusCallback { success, camera ->
            if (success) {
                focus.value = false
                Log.i("tap_to_focus", "success!")
                camera.cancelAutoFocus()
                lgd("Focus!")
            }else{
                camera.cancelAutoFocus()
                camera.setAutoFocusMoveCallback(Camera.AutoFocusMoveCallback { start, camera ->
                    if (!start) {
                        focus.value = false
                        Log.i("tap_to_focus", "success!")
                        camera.cancelAutoFocus()
                        lgd("Focus!")
                    }

                })
            }
        }


    private fun isExternalStorageReadOnly(): Boolean {
        val extStorageState = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED_READ_ONLY == extStorageState
    }
    private fun isExternalStorageAvailable(): Boolean {
        val extStorageState = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == extStorageState
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        cameraBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_camera, container, false);

        viewFinder = cameraBinding.cameraView
        spinnerAdapter =ArrayAdapter(
            requireContext(),
            R.layout.spinner_item)
        cameraBinding.thresholdSeekBar.progress = viewModel.threshold.toInt()
        cameraBinding.resolutionSpinner.adapter = spinnerAdapter
        cameraBinding.resolutionSpinner.setSelection(viewModel.resolutionPos,false)
        cameraBinding.resolutionSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                viewFinder.disableView()
                val h = (view as TextView).text.split("x")[0].toInt()
                val w = (view as TextView).text.split("x")[1].toInt()
                viewFinder.setMaxFrameSize(w, h)
                viewFinder.enableView()
                viewModel.resolutionPos = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        cameraBinding.thresholdSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                packPhoto()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // TODO Auto-generated method stub
                viewModel.threshold = progress.toDouble()
            }
        })
        cameraInfo = CameraData(this)


        cameraBinding.galleryButton.setOnClickListener {
            val viewModel: GalleryViewModel by activityViewModels()
            viewModel.context = activity
            //viewModel.refresh()
            findNavController().navigate(R.id.action_cameraFragment_to_galleryFragment)
        }

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            cameraBinding.galleryButton.isEnabled = false
        }
        // Request camera permissions
        viewFinder.visibility = SurfaceView.VISIBLE
        viewFinder.setCameraIndex(
            CameraCharacteristics.LENS_FACING_FRONT)
        viewFinder.setCvCameraViewListener(this)


        //Load setup
        savePauseFrame = viewModel.savePauseFrame
        focus.value = false
        pause = viewModel.pause
        preview = viewModel.preview
        cameraInfo.declineAcceptVisibility.set(viewModel.declineAcceptVisibility)
        cameraInfo.photoVisibility.set(viewModel.photoVisibility)
        cameraBinding.sizeView.text = viewModel.sizeText

        cameraBinding.camera = cameraInfo

        val galeryViewModel: GalleryViewModel by activityViewModels()
        galeryViewModel.context = activity
        mInfoTextView = cameraBinding.sizeView
        if(galeryViewModel.data.value == null){
            galeryViewModel.refresh()
            val binTask = LoadBinaryTask()
            binTask.execute(galeryViewModel)
        }

        return cameraBinding.root
    }


    override fun onPause() {
        super.onPause()
        viewFinder?.let { viewFinder.disableView() }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewFinder?.let { viewFinder.disableView() }
    }
    override fun onResume() {
        super.onResume()
        viewFinder?.let { viewFinder.enableView() }

    }
    override fun onCameraViewStarted(width: Int, height: Int) {
        imageMat = Mat(width, height, CvType.CV_8UC4)
        mCamera = viewFinder.mCamera
        mCamera.parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)
        var resolitionList = mCamera.parameters.supportedPreviewSizes.map { "${it.height}x${it.width}" }
        spinnerAdapter.clear()
        spinnerAdapter.addAll(resolitionList)


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
        if(!pause){
            imageMat = inputFrame!!.rgba()
            savePauseFrame = imageMat.clone()
        }else{
            imageMat = savePauseFrame.clone()
        }
        Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_RGB2GRAY);
        Imgproc.adaptiveThreshold(
            imageMat,
            imageMat,
            255.0,
            Imgproc.ADAPTIVE_THRESH_MEAN_C,
            Imgproc.THRESH_BINARY,
            15,
            viewModel.threshold
        )
        curFrame = imageMat.clone()
        return curFrame
    }
    @RequiresApi(Build.VERSION_CODES.O)
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
    //Load data to file
    @RequiresApi(Build.VERSION_CODES.O)
    fun saveToFile(){
        try {
            val galeryViewModel: GalleryViewModel by activityViewModels()

            val date = Instant.now()
            name = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss")
                .withZone(ZoneId.systemDefault()).format(date).replace(':','_')
            var path = getActivity()?.getExternalFilesDir("BinaryStorage")
            var res = true
            var devices = getStorageDirectories(context)!!
            if(devices.size> 1){
                path = File(devices!![1] + "/Android/data/com.example.binarycamera/files/BinaryStorage/")
                if(!path.isDirectory){
                    res = path.mkdir()
                }
            }
            val dataWriter = FileOutputStream(File(path
                ?: null, "${name}.dat"))
            val wrap =  DataOutputStream(dataWriter)
            wrap.writeShort(curFrame.size().width.toInt())
            wrap.writeShort(curFrame.size().height.toInt())
            wrap.writeInt(compreseBuffSize)
            wrap.writeInt(buffSize)
            dataWriter.write(codedBuff)
            dataWriter.close()
            var bm = Bitmap.createBitmap(curFrame.cols(), curFrame.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(curFrame, bm)
            galeryViewModel.data.value?.add(TileData(
                "${name}.dat", path!!.absolutePath,
                LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant(),
                galeryViewModel.adapter,galeryViewModel,bm, requireContext()
            ))
            galeryViewModel.refresh()


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun storagePhoto(data:ByteArray){
        //Data encrypting
        coder = Deflater()
        codedBuff = ByteArray(buffSize)
        coder.setInput(data)
        coder.finish()
        compreseBuffSize = coder.deflate(codedBuff)
        codedBuff = codedBuff.slice(0..compreseBuffSize-1).toByteArray()
        coder.end()
        cameraBinding.sizeView.text = "${buffSize/1000.0}KB -> ${compreseBuffSize/1000.0}KB"
        cameraBinding.sizeView.text = "${buffSize/1000.0}KB -> ${compreseBuffSize/1000.0}KB"

    }
    class LoadBinaryTask : AsyncTask<GalleryViewModel, TileData, GalleryViewModel>() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun doInBackground(vararg tiles: GalleryViewModel): GalleryViewModel {
            for(tile in tiles[0].data.value!!)
            {
                tile.img = unpack(tile.context as Activity,tile.name,tile.path)
                publishProgress(tile);
            }
            return tiles[0]
        }
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onProgressUpdate(vararg values: TileData) {
            if(values[0].vm.galery != null){
                values[0].vm.galery?.adapter = values[0].adapter
            }
            values[0].adapter.notifyItemChanged(values[0].vm.data.value?.indexOf(values[0])!!)
        }
    }
    companion object {
        val TAG = "MYLOG " + MainActivity::class.java.simpleName
        fun lgd(s: String) = Log.d(TAG, s)
        fun lge(s: String) = Log.e(TAG, s)

        fun shortMsg(context: Context, s: String) =
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
        fun unpack(context:Activity, fName:String, fPath:String): Bitmap{
            var path = File(fPath, fName)
            if(!path.exists()){
                path = File(getStorageDirectories(context)!![1] + "/Android/data/com.example.binarycamera/files/BinaryStorage/" + fName)
            }
            val dataReader = FileInputStream(path)
            val scanner = DataInputStream(dataReader)

            val height = scanner.readShort().toDouble()
            val width = scanner.readShort().toDouble()
            val compBuffSize = scanner.readInt()
            val bSize = scanner.readInt()

            val prevSize = Size(height,width)
            var res = ByteArray(compBuffSize)
            dataReader.read(res)

            //Unpack bytes
            val decoder = Inflater()
            decoder.setInput(res, 0, compBuffSize)
            val result = ByteArray(bSize)
            decoder.inflate(result)
            decoder.end()

            var prevMat = Mat(prevSize, CvType.CV_8UC1)
            prevMat.put(0,0, result)

            val displayMetrics = DisplayMetrics()
            context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics)
            val h = displayMetrics.heightPixels //2132
            val w = displayMetrics.widthPixels //1080

            var bm = Bitmap.createBitmap(prevMat.cols(), prevMat.rows(), Bitmap.Config.ARGB_8888)

            Utils.matToBitmap(prevMat, bm)
            bm = Bitmap.createScaledBitmap(bm,w,h,true)
            return bm
        }
        fun isExternalStorageAvailable(): Boolean {
            val state = Environment.getExternalStorageState()
            var mExternalStorageAvailable = false
            var mExternalStorageWriteable = false
            if (Environment.MEDIA_MOUNTED == state) {
                // We can read and write the media
                mExternalStorageWriteable = true
                mExternalStorageAvailable = mExternalStorageWriteable
            } else if (Environment.MEDIA_MOUNTED_READ_ONLY == state) {
                // We can only read the media
                mExternalStorageAvailable = true
                mExternalStorageWriteable = false
            } else {
                // Something else is wrong. It may be one of many other states, but
                // all we need
                // to know is we can neither read nor write
                mExternalStorageWriteable = false
                mExternalStorageAvailable = mExternalStorageWriteable
            }
            return (mExternalStorageAvailable && mExternalStorageWriteable)
        }
        fun getStorageDirectories(pContext: Context?): Array<String>? {
            // Final set of paths
            val rv: MutableSet<String> = HashSet()

            //Get primary & secondary external device storage (internal storage & micro SDCARD slot...)
            val listExternalDirs = ContextCompat.getExternalFilesDirs(pContext!!, null)
            for (i in listExternalDirs.indices) {
                if (listExternalDirs[i] != null) {
                    val path = listExternalDirs[i]!!.absolutePath
                    val indexMountRoot = path.indexOf("/Android/data/")
                    if (indexMountRoot >= 0 && indexMountRoot <= path.length) {
                        //Get the root path for the external directory
                        rv.add(path.substring(0, indexMountRoot))
                    }
                }
            }
            return rv.toTypedArray()
        }

        // messages:
        const val OPENCV_SUCCESSFUL = "OpenCV Loaded Successfully!"
        const val OPENCV_PROBLEM = "There's a problem in OpenCV."
        const val PERMISSION_NOT_GRANTED = "Permissions not granted by the user."

    }

}
