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
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Size
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.util.zip.Inflater


class GalleryAdapter(val vModel: GalleryViewModel): RecyclerView.Adapter<GalleryViewHolder>() {
    var recyclerRows:MutableList<TileData> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val binding:TilePreviewBinding= DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.tile_preview, parent, false);
        return GalleryViewHolder(binding);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(recyclerRows[getItemCount()-position-1],vModel.context!!);
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun refresh(tiles:MutableList<TileData>) {
        recyclerRows = tiles
    }

    override fun getItemCount(): Int {
        return recyclerRows.size
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun showPhoto(tile:TileData){
        vModel.curPhoto.value = tile.name
        }
}
class GalleryViewHolder(var tilePreviewBinding: TilePreviewBinding) :
    RecyclerView.ViewHolder(tilePreviewBinding.root) {

    fun bind(obj: Any?, context:Activity) {
        tilePreviewBinding.setVariable(BR.model, obj)
        tilePreviewBinding.photoPreview.setImageBitmap(CameraFragment.unpack(context,(obj as TileData).name+".dat"))
        tilePreviewBinding.photoPreview.rotation = 90f
        tilePreviewBinding.executePendingBindings()
    }
}