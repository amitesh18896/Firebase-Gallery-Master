package com.pallaw.firebasegallery.data.local.dao

import androidx.room.*
import com.pallaw.firebasegallery.data.resources.Photo
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Created by Pallaw Pathak on 20/04/20. - https://www.linkedin.com/in/pallaw-pathak-a6a324a1/
 */
@Dao
interface PhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(photo: Photo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: List<Photo>)

    @Delete
    fun delete(photo: Photo)

    //    @Query("DELETE FROM images")
//    fun deleteAll(photo: Photo)
//
    @Query("SELECT * from images ")
    fun getAllPhotos(): Observable<List<Photo>>
}