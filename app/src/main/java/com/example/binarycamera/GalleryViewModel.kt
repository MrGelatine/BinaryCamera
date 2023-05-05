package com.example.binarycamera

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File
import java.time.LocalDate
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
class GalleryViewModel: ViewModel() {
    var data:MutableLiveData<MutableList<TileData>> = MutableLiveData()
    var adapter:GalleryAdapter = GalleryAdapter(this)
    var context:Activity? = null
    var curPhoto: MutableLiveData<String> = MutableLiveData()
    lateinit var manager: FragmentManager
    fun refresh(context:Activity){
        this.context = context
        data.value = mutableListOf()
        for(elem in File(context?.getExternalFilesDir("BinaryStorage").toString()).walkTopDown()) {
            if (".dat".toRegex().find(elem.name) != null) {
                data.value?.add(
                    TileData(
                        elem.name,
                        LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant(),
                        adapter
                    )
                )
            }
        }
        adapter.refresh(data.value!!)
    }


}