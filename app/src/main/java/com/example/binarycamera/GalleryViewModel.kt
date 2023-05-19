package com.example.binarycamera

import android.app.Activity
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.graphics.createBitmap
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.binarycamera.databinding.FragmentGalleryBinding
import java.io.File
import java.time.LocalDate
import java.time.ZoneId


@RequiresApi(Build.VERSION_CODES.O)
class GalleryViewModel: ViewModel() {
    var data:MutableLiveData<MutableList<TileData>> = MutableLiveData()
    var adapter:GalleryAdapter = GalleryAdapter(this)
    var context:Activity? = null
    var galery:FragmentGalleryBinding? = null
    var curPhoto: MutableLiveData<TileData> = MutableLiveData()
    var showChecked:MutableLiveData<Int> = MutableLiveData(View.GONE)
    lateinit var manager: FragmentManager
    fun refresh() {
        var localPath = context?.getExternalFilesDir("BinaryStorage").toString()

        var dummyBitmap = createBitmap(1, 1)
        if (data.value == null) {
            data.value = mutableListOf()

            for (elem in File(localPath).walkTopDown()) {
                if (".dat".toRegex().find(elem.name) != null) {
                    data.value?.add(
                        TileData(
                            elem.name,
                            localPath,
                            LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant(),
                            adapter, this, dummyBitmap, context!!
                        )
                    )
                }
            }
            var devices = CameraFragment.getStorageDirectories(context)!!
            if (devices.size > 1) {
                var sdPath =
                    devices[1] + "/Android/data/com.example.binarycamera/files/BinaryStorage/"
                if (sdPath != null) {
                    for (elem in File(sdPath).walkTopDown()) {
                        if (".dat".toRegex().find(elem.name) != null) {
                            data.value?.add(
                                TileData(
                                    elem.name,
                                    sdPath,
                                    LocalDate.now().atStartOfDay(ZoneId.systemDefault())
                                        .toInstant(),
                                    adapter, this, dummyBitmap, context!!
                                )
                            )
                        }
                    }
                }
            }
        }

        adapter.refresh(data.value!!)
    }
}
