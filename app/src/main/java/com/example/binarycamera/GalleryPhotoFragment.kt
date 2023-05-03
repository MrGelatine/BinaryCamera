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
import com.example.binarycamera.databinding.FragmentGalleryPhotoBinding


class GalleryPhotoFragment : Fragment() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding:FragmentGalleryPhotoBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_gallery_photo, container, false);
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().popBackStack()
        }
        val viewModel: GalleryViewModel by activityViewModels()
        binding.photoView.setImageBitmap(activity?.let { CameraFragment.unpack(it,viewModel.curPhoto.value+".dat") })
        binding.photoView.rotation = 90f
        return binding.root
    }
}