package com.example.binarycamera

import java.time.Instant

data class TileData(var name:String = "", var date: Instant? = null, var adapter:GalleryAdapter) {

}