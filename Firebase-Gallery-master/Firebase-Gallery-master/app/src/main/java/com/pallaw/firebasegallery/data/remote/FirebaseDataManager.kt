package com.pallaw.firebasegallery.data.remote

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.pallaw.firebasegallery.data.resources.Photo
import io.reactivex.*


/**
 * Created by Pallaw Pathak on 20/04/20. - https://www.linkedin.com/in/pallaw-pathak-a6a324a1/
 */
class FirebaseDataManager(
    val mDataBase: DatabaseReference,
    val mFileBucket: StorageReference
) {

    var isPhotoUploadedSuccessfully: MutableLiveData<Boolean> = MutableLiveData()

    fun uploadPhoto(photoUri: Uri): Single<Photo> {
        return Single.create<Photo> { call ->
            val fileReference: StorageReference =
                mFileBucket.child(photoUri.lastPathSegment.toString())
            fileReference.putFile(photoUri)
                .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot -> // Get a URL to the uploaded content
                    fileReference.downloadUrl.addOnSuccessListener { downloadUrl ->
                        val url = downloadUrl.toString()
                        val uploadId: String? = mDataBase.push().key

                        uploadId?.let { uploadedId ->
                            val photo = Photo(
                                uploadedId,
                                photoUri.lastPathSegment.toString(),
                                url
                            )
                            mDataBase.child(uploadedId)
                                .setValue(
                                    photo
                                )
                                .addOnFailureListener { exception -> call.onError(exception) }
                                .addOnSuccessListener { call.onSuccess(photo) }
                        }
                    }
                })
                .addOnFailureListener { exception -> call.onError(exception) }
        }
    }

    fun getPhotos(): Observable<List<Photo>> {
        return Observable.create<List<Photo>> {emitter ->
            mDataBase.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    emitter.onError(error.toException())
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    val photoList: ArrayList<Photo> = arrayListOf()
                    for (ds in snapshot.getChildren()) {
                        val snap: Photo? = ds.getValue(Photo::class.java)
                        snap?.let {
                            photoList.add(it)
                        }
                    }

                    emitter.onNext(photoList)
                }
            })
        }

    }

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: FirebaseDataManager? = null

        fun getInstance(context: Context): FirebaseDataManager {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val firebaseDataManager = FirebaseDataManager(
                    FirebaseDatabase.getInstance().getReference("images"),
                    FirebaseStorage.getInstance().getReference("images")
                )
                INSTANCE = firebaseDataManager
                return firebaseDataManager
            }
        }
    }

}