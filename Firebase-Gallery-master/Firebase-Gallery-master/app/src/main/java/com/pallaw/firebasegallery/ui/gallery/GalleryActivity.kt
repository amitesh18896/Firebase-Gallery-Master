package com.pallaw.firebasegallery.ui.gallery

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.pallaw.firebasegallery.R
import com.pallaw.firebasegallery.Util.getTitleView
import com.pallaw.firebasegallery.data.resources.Photo
import com.pallaw.firebasegallery.viewmodel.PhotoViewModel
import com.pallaw.firebasegallery.viewmodel.factory.PhotoViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*


class GalleryActivity : AppCompatActivity(),
    GalleryGridFragment.onGalleryItemClickListener {

    private lateinit var mDatabaseRef: DatabaseReference
    private lateinit var mFileBucketRef: StorageReference

    private val viewModel: PhotoViewModel by lazy {
        ViewModelProvider(
            viewModelStore,
            PhotoViewModelFactory(application)
        ).get(PhotoViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // init firebase variables
        initFirebase()

        //setActions
        setActions()

    }

    override fun onResume() {
        super.onResume()
        supportActionBar?.let { actionBar ->

            //set title
            viewModel.title.observe(this,
                Observer<String> { title ->
                    actionBar.title = title
                    animateToolbarTitle()
                })

            //toggle back button visibility
            viewModel.backButtonVisibility.observe(this,
                Observer<Boolean> { enable -> actionBar.setDisplayHomeAsUpEnabled(enable) })
        }

        // toggle fab button visibility
        viewModel.fabVisibility.observe(this,
            Observer<Boolean> { enable -> if (enable) fab.show() else fab.hide() })
    }

    private fun animateToolbarTitle() {
        val scaleX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 100f, 0f)
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
        ObjectAnimator.ofPropertyValuesHolder(toolbar.getTitleView(), scaleX, alpha).apply {
            interpolator = OvershootInterpolator()
        }.setDuration(400).start()
    }

    private fun setActions() {
        fab.setOnClickListener { view ->
            pickImage()
        }
    }

    private fun pickImage() {
        ImagePicker.with(this)
            .crop()                    //Crop image(Optional), Check Customization for more option
            .compress(1024)   //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            )    //Final image resolution will be less than 1080 x 1080(Optional)
            .start()
    }

    private fun initFirebase() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("images")
        mFileBucketRef = FirebaseStorage.getInstance().getReference("images")
    }

    override fun onGalleryItemClicked(item: Photo) {
        val toGalleryZoom = GalleryGridFragmentDirections.toGalleryZoom(item)
        findNavController(R.id.nav_host_fragment).navigate(toGalleryZoom)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        handlePickedImage(resultCode, data)

    }

    private fun handlePickedImage(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data
            fileUri?.let {
                uploadImageToFirebase(it)
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToFirebase(file: Uri) {
        viewModel.uploadNewPhoto(file).observeOn(AndroidSchedulers.mainThread()).subscribe({
            Toast.makeText(applicationContext, "Image uploaded successfully", Toast.LENGTH_LONG)
                .show()
        }, {
            Toast.makeText(applicationContext, "Image uploaded error", Toast.LENGTH_LONG).show()
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return true
    }

}