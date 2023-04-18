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
            findNavController().popBackStack()
        }
        return binding.root
    }
}