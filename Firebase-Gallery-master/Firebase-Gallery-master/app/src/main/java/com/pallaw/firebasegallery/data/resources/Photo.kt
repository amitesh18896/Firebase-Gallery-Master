package com.pallaw.firebasegallery.data.resources

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable


/**
 * Created by Pallaw Pathak on 20/04/20. - https://www.linkedin.com/in/pallaw-pathak-a6a324a1/
 */
@IgnoreExtraProperties
@Entity(tableName = "images")
data class Photo(
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    var id: String = "",
    @ColumnInfo(name = "name")
    var name: String = "",
    @ColumnInfo(name = "url")
    var url: String = "",
    var fileUri: String? = null
) : Serializable