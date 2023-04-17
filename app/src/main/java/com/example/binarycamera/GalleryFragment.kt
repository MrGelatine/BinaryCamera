package com.example.binarycamera

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.binarycamera.databinding.FragmentGalleryBinding

class GalleryFragment() : Fragment() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding:FragmentGalleryBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_gallery, container, false);
        val viewModel: GalleryViewModel by activityViewModels()
        viewModel.context = activity
        binding.adapter = viewModel.adapter
        viewModel.data.observe(viewLifecycleOwner) {
            viewModel.adapter.refresh(it)
        }
        return binding.root
    }
}