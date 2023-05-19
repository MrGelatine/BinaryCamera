package com.example.binarycamera

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.binarycamera.databinding.FragmentGalleryBinding
import java.io.File


class GalleryFragment() : Fragment() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding:FragmentGalleryBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_gallery, container, false);
        val viewModel: GalleryViewModel by activityViewModels()
        viewModel.galery = binding
        viewModel.context = activity
        var data = viewModel.data.value!!
        viewModel.adapter.notifyDataSetChanged()
        binding.adapter = viewModel.adapter
        viewModel.data.observe(viewLifecycleOwner) {
            viewModel.adapter.refresh(it)
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if(viewModel.showChecked.value == View.VISIBLE){
                viewModel.showChecked.value = View.GONE
                for(tile in viewModel.data.value!!){
                    tile.checked.set(false)
                }
            }else{
                findNavController().popBackStack()
            }
        }
        viewModel.showChecked.observe(viewLifecycleOwner) {
            for(elem in viewModel.data.value!!){
                elem.checkedVisibility.set(it)
            }
            binding.deleteButton.visibility = it

        }
        binding.deleteButton.setOnClickListener{
            for(elem in viewModel.data.value!!.toList()){
                if(elem.checked.get()){
                    val ind = viewModel.data.value!!.indexOf(elem)
                    viewModel.data.value!!.removeAt(ind)
                    File(elem.path, elem.name).delete()
                }
            }
            viewModel.showChecked.value = View.GONE
            viewModel.refresh()
        }

        return binding.root
    }

}