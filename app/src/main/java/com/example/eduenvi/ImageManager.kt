package com.example.eduenvi

import android.app.Activity
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.eduenvi.models.Image
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageManager {

    fun setImage(url: String, context: Activity, imageView: ImageView) {
        Glide.with(context)
            .load(url)
            .into(imageView)
    }

    fun setImage(imageId: Int?, context: Activity, imageView: ImageView) {
        if (imageId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val dbImage: Image? = ApiHelper.getImage(imageId)
                withContext(Dispatchers.Main) {
                    if (dbImage != null) setImage(dbImage.url, context, imageView)
                }
            }
        }
    }
}