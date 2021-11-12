package com.pallaw.firebasegallery.ui.gallery

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.pallaw.firebasegallery.R
import com.pallaw.firebasegallery.Util.LogTags
import com.pallaw.firebasegallery.data.resources.Photo
import com.pallaw.firebasegallery.data.resources.PhotoList
import com.pallaw.firebasegallery.viewmodel.PhotoViewModel
import com.pallaw.firebasegallery.viewmodel.factory.PhotoViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_gallery.*
import timber.log.Timber
import java.net.ConnectException
import java.net.UnknownHostException

class GalleryGridFragment : Fragment() {

    private lateinit var galleryGridAdapter: GalleryGridAdapter
    private lateinit var viewModel: PhotoViewModel
    private lateinit var mStorageRef: StorageReference
    val compositeDisposable = CompositeDisposable()
    val photoList: ArrayList<Photo> = arrayListOf()

    private var listener: onGalleryItemClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStorageRef = FirebaseStorage.getInstance().reference

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //init viewmodel
        initViewModel()

        //update title
        updateToolbar()

        //setup photo list
        setupPhotoList()

        //fetch photo list
        fetchPhotos()
    }

    private fun fetchPhotos() {
        compositeDisposable.add(
            viewModel.getAllPhotos()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Timber.tag(LogTags.PHOTO).d("Received UIModel $it photos.")
                    showPhotos(it)
                }, {
                    Timber.tag(LogTags.PHOTO).w(it)
                    showError()
                })
        )
    }

    private fun updateToolbar() {
        viewModel.updateTitle(getString(R.string.gallary_fragment_label))
        viewModel.enableFab(true)
        viewModel.enableBackButton(false)
    }

    private fun showError() {
        Toast.makeText(context, "Error in fetching photos", Toast.LENGTH_LONG).show()
    }

    private fun showPhotos(data: PhotoList) {
        if (data.error == null) {
            photoList.clear()
            photoList.addAll(data.photos)
            galleryGridAdapter.notifyDataSetChanged()
        } else if (data.error is ConnectException || data.error is UnknownHostException) {
            Timber.tag(LogTags.PHOTO).d("No internet connection, cached data is being shown")
        } else {
            showError()
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

    private fun setupPhotoList() {
        galleryGridAdapter = GalleryGridAdapter(
            photoList,
            listener
        )
        with(photo_list) {
            layoutManager = GridLayoutManager(context, 2)
            adapter = galleryGridAdapter
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is onGalleryItemClickListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        compositeDisposable.clear()
    }

    interface onGalleryItemClickListener {
        fun onGalleryItemClicked(item: Photo)
    }

}
