package com.pallaw.firebasegallery.ui.gallery


import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.pallaw.firebasegallery.R
import com.pallaw.firebasegallery.Util.loadImage
import com.pallaw.firebasegallery.data.resources.Photo
import com.pallaw.firebasegallery.ui.gallery.GalleryGridFragment.onGalleryItemClickListener
import kotlinx.android.synthetic.main.item_photo.view.*

class GalleryGridAdapter(
    private val mValues: List<Photo>,
    private val mListener: onGalleryItemClickListener?
) : RecyclerView.Adapter<GalleryGridAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Photo
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onGalleryItemClicked(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]

        holder.bindData(item)

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {

        val image: ImageView = mView.gallary_item_image

        fun bindData(photo: Photo) {
            if (null == photo.fileUri) {
                image.loadImage(photo.url)
            } else {
                image.loadImage(Uri.parse(photo.fileUri))
            }
        }
    }
}
