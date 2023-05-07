package com.example.binarycamera

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
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

        viewModel.data.observe(viewLifecycleOwner) {
            viewModel.adapter.refresh(it)
        }
        viewModel.context = activity
        binding.adapter = viewModel.adapter

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
            for(elem in viewModel.data.value!!){
                if(elem.checked.get()){
                    File(activity?.getExternalFilesDir("BinaryStorage"), elem.name).delete()
                }
            }
            viewModel.showChecked.value = View.GONE
            viewModel.refresh(activity?.getExternalFilesDir("BinaryStorage").toString())
        }
        return binding.root
    }
}