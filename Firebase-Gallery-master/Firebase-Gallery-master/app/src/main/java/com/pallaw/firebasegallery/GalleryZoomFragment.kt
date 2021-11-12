package com.pallaw.firebasegallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pallaw.firebasegallery.Util.loadImage
import com.pallaw.firebasegallery.viewmodel.PhotoViewModel
import com.pallaw.firebasegallery.viewmodel.factory.PhotoViewModelFactory
import kotlinx.android.synthetic.main.fragment_gallery_zoom.*

/**
 * A simple [Fragment] subclass.
 */
class GalleryZoomFragment : Fragment() {

    private lateinit var viewModel: PhotoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery_zoom, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //init viewModel
        initViewModel()

        arguments?.let {
            val fromBundle = GalleryZoomFragmentArgs.fromBundle(it)
            val photo = fromBundle.photo
            viewModel.updateTitle(photo.name)
            viewModel.enableFab(false)
            viewModel.enableBackButton(true)
            photo_zoom.loadImage(photo.url)
        }
    }

    private fun initViewModel() {
        activity?.let {
            viewModel = ViewModelProvider(
                it.viewModelStore,
                PhotoViewModelFactory(it.application)
            ).get(PhotoViewModel::class.java)
        }
    }

}
