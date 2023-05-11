package com.example.binarycamera

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Environment
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File
import java.lang.Exception
import java.time.LocalDate
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
class GalleryViewModel: ViewModel() {
    var data:MutableLiveData<MutableList<TileData>> = MutableLiveData()
    var adapter:GalleryAdapter = GalleryAdapter(this)
    var context:Activity? = null
    var curPhoto: MutableLiveData<String> = MutableLiveData()
    var showChecked:MutableLiveData<Int> = MutableLiveData(View.GONE)
    lateinit var manager: FragmentManager
    fun refresh(context: Context?){
        var localPath = context?.getExternalFilesDir("BinaryStorage").toString()


        data.value = mutableListOf()
        for(elem in File(localPath).walkTopDown()) {
            if (".dat".toRegex().find(elem.name) != null) {
                data.value?.add(
                    TileData(
                        elem.name,
                        LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant(),
                        adapter,this
                    )
                )
            }
        }
        var devices = CameraFragment.getStorageDirectories(context)!!
        if(devices.size> 1){
            var sdPath = devices[1] + "/Android/data/com.example.binarycamera/files/BinaryStorage/"
            if(sdPath != null){
                for(elem in File(sdPath).walkTopDown()) {
                    if (".dat".toRegex().find(elem.name) != null) {
                        data.value?.add(
                            TileData(
                                elem.name,
                                LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant(),
                                adapter,this
                            )
                        )
                    }
                }
            }
        }

        adapter.refresh(data.value!!)
    }


}