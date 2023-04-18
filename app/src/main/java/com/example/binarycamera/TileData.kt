package com.example.binarycamera

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.navigation.findNavController
import java.time.Instant

data class TileData(var name:String = "", var date: Instant? = null, var adapter:GalleryAdapter) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun showPhoto(v:View){
        adapter.vModel.curPhoto.value = name
        adapter.showPhoto(this)
        v.findNavController().navigate(R.id.galleryPhotoFragment)
    }


}