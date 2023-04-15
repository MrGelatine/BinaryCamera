package com.example.binarycamera

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.binarycamera.databinding.TilePreviewBinding

class GalleryAdapter(): RecyclerView.Adapter<GalleryViewHolder>() {
    var recyclerRows:MutableList<TileData> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val binding:TilePreviewBinding= DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.tile_preview, parent, false);

        return GalleryViewHolder(binding);
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(recyclerRows[position]);
    }
    fun remove(row: TileData){
        val ind = recyclerRows.indexOf(row)
        recyclerRows.removeAt(ind)
        notifyItemRemoved(ind)

    }
    fun refresh(data: MutableList<TileData>){
        recyclerRows = data
    }

    override fun getItemCount(): Int {
        return recyclerRows.size
    }
}
class GalleryViewHolder(var tilePreviewBinding: TilePreviewBinding) :
    RecyclerView.ViewHolder(tilePreviewBinding.root) {

    fun bind(obj: Any?) {
        tilePreviewBinding.setVariable(BR.model, obj)
        tilePreviewBinding.executePendingBindings()
    }
}