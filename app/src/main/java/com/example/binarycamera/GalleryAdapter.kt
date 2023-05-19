package com.example.binarycamera

import android.app.Activity
import android.graphics.Bitmap
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.binarycamera.databinding.TilePreviewBinding


class GalleryAdapter(val vModel: GalleryViewModel): RecyclerView.Adapter<GalleryViewHolder>() {
    var recyclerRows:MutableList<TileData> = mutableListOf()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val binding:TilePreviewBinding= DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.tile_preview, parent, false);
        return GalleryViewHolder(binding);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(recyclerRows[itemCount - position-1],vModel.context!!);
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun refresh(tiles:MutableList<TileData>) {
        recyclerRows = tiles
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return recyclerRows.size
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun showPhoto(tile:TileData){
        vModel.curPhoto.value = tile
        }
}
class GalleryViewHolder(var tilePreviewBinding: TilePreviewBinding) :
    RecyclerView.ViewHolder(tilePreviewBinding.root) {

    fun bind(obj: Any?, context:Activity) {
        tilePreviewBinding.setVariable(BR.model, obj)
        var tile = (obj as TileData).img
        tilePreviewBinding.photoPreview.setImageBitmap(Bitmap.createScaledBitmap(tile,tile.width,tile.width,false))
        tilePreviewBinding.photoPreview.rotation = 90f
        tilePreviewBinding.executePendingBindings()
    }
}