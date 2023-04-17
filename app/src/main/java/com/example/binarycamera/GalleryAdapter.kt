package com.example.binarycamera

import android.R.attr.angle
import android.R.attr.pivotX
import android.R.attr.pivotY
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
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
    fun remove(row: TileData){
        val ind = recyclerRows.indexOf(row)
        recyclerRows.removeAt(ind)
        notifyItemRemoved(ind)

    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun refresh(tileData: MutableList<TileData>) {
        recyclerRows = vModel.data.value!!
    }

    override fun getItemCount(): Int {
        return recyclerRows.size
    }
}
class GalleryViewHolder(var tilePreviewBinding: TilePreviewBinding) :
    RecyclerView.ViewHolder(tilePreviewBinding.root) {

    fun bind(obj: Any?, context:Activity) {
        tilePreviewBinding.setVariable(BR.model, obj)

        val dataReader = FileInputStream(File(context.getExternalFilesDir("BinaryStorage") ?: null, (obj as TileData).name+".dat"))
        val scanner = DataInputStream(dataReader)
        val height = scanner.readInt().toDouble()
        val width = scanner.readInt().toDouble()
        val prevSize = Size(height,width)
        val dada = DataInputStream(dataReader)
        val compBuffSize = dada.readInt()
        val bSize = dada.readInt()
        var res = ByteArray(compBuffSize)
        dataReader.read(res)

        //Unpack bytes
        val decoder = Inflater()
        decoder.setInput(res, 0, compBuffSize)
        val result = ByteArray(bSize)
        decoder.inflate(result)
        decoder.end()

        var prevMat = Mat(prevSize, CvType.CV_8UC1)
        prevMat.put(0,0, result)
        val bm = Bitmap.createBitmap(prevMat.cols(), prevMat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(prevMat, bm)

        tilePreviewBinding.photoPreview.setImageBitmap(bm)

        tilePreviewBinding.photoPreview.rotation = 90f

        tilePreviewBinding.executePendingBindings()
    }
}