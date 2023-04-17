package com.example.binarycamera

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query

@RequiresApi(Build.VERSION_CODES.O)
class GalleryViewModel: ViewModel() {
    val adapter = GalleryAdapter(this)
    var context:Activity? = null
    var data:MutableLiveData<MutableList<TileData>> = MutableLiveData()
    var realm:Realm = Realm.open(RealmConfiguration.create(schema = setOf(PhotoRealmObject::class)))
    init{
        data.value = realm.copyFromRealm(realm.query<PhotoRealmObject>().find()).map{it.toTileData(adapter)!!}.toMutableList()
    }
    fun refresh(){
        data.value = realm.copyFromRealm(realm.query<PhotoRealmObject>().find()).map{it.toTileData(adapter)!!}.toMutableList()
    }
}