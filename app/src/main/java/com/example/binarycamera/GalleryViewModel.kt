package com.example.binarycamera

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query

@RequiresApi(Build.VERSION_CODES.O)
class GalleryViewModel: ViewModel() {
    var realm:Realm = Realm.open(RealmConfiguration.create(schema = setOf(PhotoRealmObject::class)))
    var data:MutableLiveData<MutableList<TileData>> = MutableLiveData()
    var adapter:GalleryAdapter = GalleryAdapter(this)
    var context:Activity? = null
    var curPhoto: MutableLiveData<String> = MutableLiveData()
    lateinit var manager: FragmentManager
    init{
        data.value = realm.copyFromRealm(realm.query<PhotoRealmObject>().find()).map{it.toTileData(adapter)!!}.toMutableList()
    }
    fun refresh(){
        data.value = realm.copyFromRealm(realm.query<PhotoRealmObject>().find()).map{it.toTileData(adapter)!!}.toMutableList()
        adapter.refresh(data.value!!)
    }
}