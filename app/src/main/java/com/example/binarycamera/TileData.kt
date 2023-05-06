package com.example.binarycamera

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import java.time.Instant

data class TileData(var name:String = "", var date: Instant? = null, var adapter:GalleryAdapter,var vm:GalleryViewModel) {
    var checked:ObservableBoolean = ObservableBoolean(false)
    @RequiresApi(Build.VERSION_CODES.O)
    var checkedVisibility:ObservableInt = ObservableInt(vm.showChecked.value!!)
    @RequiresApi(Build.VERSION_CODES.O)
    fun showPhoto(v:View){
        adapter.vModel.curPhoto.value = name
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