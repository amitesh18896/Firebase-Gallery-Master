package com.pallaw.firebasegallery.data.resources

/**
 * Created by Pallaw Pathak on 21/04/20. - https://www.linkedin.com/in/pallaw-pathak-a6a324a1/
 */
data class PhotoList(val photos: List<Photo>, val message: String, val error: Throwable? = null)