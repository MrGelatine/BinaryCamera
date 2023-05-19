package com.example.binarycamera

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.navigation.findNavController
import java.time.Instant

data class TileData(var name:String = "", var path:String = "", var date: Instant? = null, var adapter:GalleryAdapter, var vm:GalleryViewModel, var img:Bitmap,
                    val context:Context) {
    var checked:ObservableBoolean = ObservableBoolean(false)
    @RequiresApi(Build.VERSION_CODES.O)
    var checkedVisibility:ObservableInt = ObservableInt(vm.showChecked.value!!)
    @RequiresApi(Build.VERSION_CODES.O)
    fun showPhoto(v:View){
        adapter.vModel.curPhoto.value = this
        adapter.showPhoto(this)
        v.findNavController().navigate(R.id.galleryPhotoFragment)
    }
    fun checkPhoto() {
        checked.set(!checked.get()!!)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun setChoose(): Boolean {
        vm.showChecked.value = View.VISIBLE
        checked.set(true)
        return true
    }


}